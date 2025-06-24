package org.bruwave.abacusflow.usecase.transaction.service.impl

import org.bruwave.abacusflow.db.inventory.InventoryUnitRepository
import org.bruwave.abacusflow.db.partner.CustomerRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.generated.jooq.Tables.CUSTOMERS
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCTS
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDERS
import org.bruwave.abacusflow.generated.jooq.Tables.SALE_ORDERS
import org.bruwave.abacusflow.generated.jooq.Tables.SALE_ORDER_ITEMS
import org.bruwave.abacusflow.generated.jooq.Tables.SUPPLIERS
import org.bruwave.abacusflow.transaction.OrderStatus
import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toTO
import org.bruwave.abacusflow.usecase.transaction.service.SaleOrderQueryService
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID
import javax.management.Query.eq

@Service
class SaleOrderQueryServiceImpl(
    private val saleOrderRepository: SaleOrderRepository,
    private val customerRepository: CustomerRepository,
    private val jooqDsl: DSLContext,
) : SaleOrderQueryService {
    override fun listSaleOrdersPage(
        pageable: Pageable,
        orderNo: UUID?,
        customerName: String?,
        status: String?,
        productName: String?
    ): Page<BasicSaleOrderTO> {
        val conditions = mutableListOf<Condition>()

        orderNo?.let {
            conditions += PURCHASE_ORDERS.NO.eq(it)
        }

        customerName?.takeIf { it.isNotBlank() }?.let {
            conditions += CUSTOMERS.NAME.containsIgnoreCase(it)
        }

        status?.takeIf { it.isNotBlank() }?.let {
            conditions += PURCHASE_ORDERS.STATUS.eq(it)
        }

        productName?.takeIf { it.isNotBlank() }?.let {
            conditions += PRODUCTS.NAME.containsIgnoreCase(it)
        }

        val joinedTables = SALE_ORDERS
            .leftJoin(CUSTOMERS).on(SALE_ORDERS.CUSTOMER_ID.eq(CUSTOMERS.ID))
            .leftJoin(SALE_ORDER_ITEMS).on(SALE_ORDER_ITEMS.ORDER_ID.eq(SALE_ORDERS.ID))

        val total = jooqDsl
            .select(DSL.countDistinct(SALE_ORDERS.ID))
            .from(joinedTables)
            .fetchOne(0, Long::class.java) ?: 0L

        val totalAmountField = DSL.sum(SALE_ORDER_ITEMS.UNIT_PRICE.mul(SALE_ORDER_ITEMS.QUANTITY)).`as`("total_amount")
        val totalQuantityField = DSL.sum(SALE_ORDER_ITEMS.QUANTITY).`as`("total_quantity")
        val itemCountField = DSL.countDistinct(SALE_ORDER_ITEMS.ID).`as`("item_count")

        val records = jooqDsl
            .select(
                SALE_ORDERS.ID,
                SALE_ORDERS.NO,
                SALE_ORDERS.STATUS,
                SALE_ORDERS.ORDER_DATE,
                SALE_ORDERS.CREATED_AT,
                CUSTOMERS.NAME,
                totalAmountField,
                totalQuantityField,
                itemCountField
            )
            .from(joinedTables)
            .groupBy(
                SALE_ORDERS.ID,
                SALE_ORDERS.NO,
                SALE_ORDERS.STATUS,
                SALE_ORDERS.ORDER_DATE,
                SALE_ORDERS.CREATED_AT,
                CUSTOMERS.NAME
            )
            .orderBy(SALE_ORDERS.CREATED_AT.desc())
            .offset(pageable.offset)
            .limit(pageable.pageSize)
            .fetch()
            .map {
                val status = it[SALE_ORDERS.STATUS]!!
                val orderDate = it[SALE_ORDERS.ORDER_DATE]!!
                val autoCompleteDate = if (status == OrderStatus.PENDING.name) orderDate.plusDays(7) else null

                BasicSaleOrderTO(
                    id = it[SALE_ORDERS.ID]!!,
                    orderNo = it[SALE_ORDERS.NO]!!,
                    customerName = it[CUSTOMERS.NAME] ?: "unknown",
                    status = status,
                    orderDate = orderDate,
                    createdAt = it[SALE_ORDERS.CREATED_AT]!!.toInstant(),
                    totalAmount = it.get("total_amount", BigDecimal::class.java) ?: BigDecimal.ZERO,
                    totalQuantity = it.get("total_quantity", Long::class.java) ?: 0L,
                    itemCount = it.get("item_count", Int::class.java) ?: 0,
                    autoCompleteDate = autoCompleteDate
                )
            }

        return PageImpl(records, pageable, total)
    }

    override fun getSaleOrder(id: Long): SaleOrderTO =
        saleOrderRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }
            .toTO()
}

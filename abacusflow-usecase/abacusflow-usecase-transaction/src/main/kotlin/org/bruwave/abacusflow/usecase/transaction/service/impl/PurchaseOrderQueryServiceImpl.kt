package org.bruwave.abacusflow.usecase.transaction.service.impl

import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCTS
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDERS
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDER_ITEMS
import org.bruwave.abacusflow.generated.jooq.Tables.SUPPLIERS
import org.bruwave.abacusflow.transaction.OrderStatus
import org.bruwave.abacusflow.usecase.transaction.BasicPurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toTO
import org.bruwave.abacusflow.usecase.transaction.service.PurchaseOrderQueryService
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.UUID

@Service
class PurchaseOrderQueryServiceImpl(
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val jooqDsl: DSLContext,
) : PurchaseOrderQueryService {
    override fun listPurchaseOrdersPage(
        pageable: Pageable,
        orderNo: UUID?,
        supplierName: String?,
        status: String?,
        productName: String?,
    ): Page<BasicPurchaseOrderTO> {
        val conditions = mutableListOf<Condition>()

        orderNo?.let {
            conditions += PURCHASE_ORDERS.NO.eq(it)
        }

        supplierName?.takeIf { it.isNotBlank() }?.let {
            conditions += SUPPLIERS.NAME.containsIgnoreCase(it)
        }

        status?.takeIf { it.isNotBlank() }?.let {
            conditions += PURCHASE_ORDERS.STATUS.eq(it)
        }

        productName?.takeIf { it.isNotBlank() }?.let {
            conditions += PRODUCTS.NAME.containsIgnoreCase(it)
        }

        val joinedTables =
            PURCHASE_ORDERS
                .leftJoin(SUPPLIERS).on(PURCHASE_ORDERS.SUPPLIER_ID.eq(SUPPLIERS.ID))
                .leftJoin(PURCHASE_ORDER_ITEMS).on(PURCHASE_ORDER_ITEMS.ORDER_ID.eq(PURCHASE_ORDERS.ID))
                .leftJoin(PRODUCTS).on(PURCHASE_ORDER_ITEMS.PRODUCT_ID.eq(PRODUCTS.ID))

        val total =
            jooqDsl
                .select(DSL.countDistinct(PURCHASE_ORDERS.ID))
                .from(joinedTables)
                .where(conditions)
                .fetchOne(0, Long::class.java) ?: 0L

        val totalAmountField =
            DSL.sum(PURCHASE_ORDER_ITEMS.UNIT_PRICE.mul(PURCHASE_ORDER_ITEMS.QUANTITY)).`as`("total_amount")
        val totalQuantityField = DSL.sum(PURCHASE_ORDER_ITEMS.QUANTITY).`as`("total_quantity")
        val itemCountField = DSL.countDistinct(PURCHASE_ORDER_ITEMS.ID).`as`("item_count")

        val records =
            jooqDsl
                .select(
                    PURCHASE_ORDERS.ID,
                    PURCHASE_ORDERS.NO,
                    PURCHASE_ORDERS.STATUS,
                    PURCHASE_ORDERS.CREATED_AT,
                    PURCHASE_ORDERS.ORDER_DATE,
                    SUPPLIERS.NAME,
                    totalAmountField,
                    totalQuantityField,
                    itemCountField,
                )
                .from(joinedTables)
                .where(conditions)
                .groupBy(
                    PURCHASE_ORDERS.ID,
                    PURCHASE_ORDERS.NO,
                    PURCHASE_ORDERS.STATUS,
                    PURCHASE_ORDERS.CREATED_AT,
                    PURCHASE_ORDERS.ORDER_DATE,
                    SUPPLIERS.NAME,
                )
                .orderBy(PURCHASE_ORDERS.CREATED_AT.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize)
                .fetch()
                .map {
                    val status = it[PURCHASE_ORDERS.STATUS]!!
                    val orderDate = it[PURCHASE_ORDERS.ORDER_DATE]!!
                    val autoCompleteDate = if (status == OrderStatus.PENDING.name) orderDate.plusDays(7) else null

                    BasicPurchaseOrderTO(
                        id = it[PURCHASE_ORDERS.ID]!!,
                        orderNo = it[PURCHASE_ORDERS.NO]!!,
                        status = it[PURCHASE_ORDERS.STATUS]!!,
                        createdAt = it[PURCHASE_ORDERS.CREATED_AT]!!.toInstant(),
                        orderDate = it[PURCHASE_ORDERS.ORDER_DATE],
                        autoCompleteDate = autoCompleteDate,
                        supplierName = it[SUPPLIERS.NAME] ?: "unknown",
                        totalAmount = it.get("total_amount", BigDecimal::class.java) ?: BigDecimal.ZERO,
                        totalQuantity = it.get("total_quantity", Long::class.java) ?: 0,
                        itemCount = it.get("item_count", Int::class.java) ?: 0,
                    )
                }

        return PageImpl(records, pageable, total)
    }

    override fun getPurchaseOrder(id: Long): PurchaseOrderTO =
        purchaseOrderRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
            .toTO()
}

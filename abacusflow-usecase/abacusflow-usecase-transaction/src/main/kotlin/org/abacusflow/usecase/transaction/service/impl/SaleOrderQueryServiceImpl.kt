package org.abacusflow.usecase.transaction.service.impl

import org.abacusflow.db.transaction.SaleOrderRepository
import org.abacusflow.generated.jooq.Tables.CUSTOMER
import org.abacusflow.generated.jooq.Tables.INVENTORY
import org.abacusflow.generated.jooq.Tables.INVENTORY_UNIT
import org.abacusflow.generated.jooq.Tables.PRODUCT
import org.abacusflow.generated.jooq.Tables.SALE_ORDER
import org.abacusflow.generated.jooq.Tables.SALE_ORDER_ITEM
import org.abacusflow.generated.jooq.enums.OrderStatusDbEnum
import org.abacusflow.transaction.OrderStatus
import org.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.abacusflow.usecase.transaction.SaleOrderTO
import org.abacusflow.usecase.transaction.mapper.toTO
import org.abacusflow.usecase.transaction.service.SaleOrderQueryService
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

@Service
class SaleOrderQueryServiceImpl(
    private val saleOrderRepository: SaleOrderRepository,
    private val jooqDsl: DSLContext,
) : SaleOrderQueryService {
    override fun listBasicSaleOrdersPage(
        pageable: Pageable,
        orderNo: UUID?,
        customerName: String?,
        status: String?,
        inventoryUnitName: String?,
        orderDate: LocalDate?,
    ): Page<BasicSaleOrderTO> {
        val conditions =
            mutableListOf<Condition>().apply {
                orderNo?.let {
                    add(SALE_ORDER.NO.eq(it))
                }

                orderDate?.let {
                    add(SALE_ORDER.ORDER_DATE.eq(it))
                }

                customerName?.takeIf { it.isNotBlank() }?.let {
                    add(CUSTOMER.NAME.containsIgnoreCase(it))
                }

                status?.takeIf { it.isNotBlank() }?.let {
                    val statusEnum =
                        when (it.uppercase()) {
                            "PENDING" -> OrderStatusDbEnum.PENDING
                            "COMPLETED" -> OrderStatusDbEnum.COMPLETED
                            "CANCELED" -> OrderStatusDbEnum.CANCELED
                            "REVERSED" -> OrderStatusDbEnum.REVERSED
                            else -> throw IllegalArgumentException("Order status not supported: $it")
                        }
                    add(SALE_ORDER.STATUS.eq(statusEnum))
                }

                inventoryUnitName?.takeIf { it.isNotBlank() }?.let {
                    val existsSubquery =
                        DSL.selectOne()
                            .from(SALE_ORDER_ITEM)
                            .join(INVENTORY_UNIT).on(SALE_ORDER_ITEM.INVENTORY_UNIT_ID.eq(INVENTORY_UNIT.ID))
                            .join(INVENTORY).on(INVENTORY_UNIT.INVENTORY_ID.eq(INVENTORY.ID))
                            .join(PRODUCT).on(INVENTORY.PRODUCT_ID.eq(PRODUCT.ID))
                            .where(SALE_ORDER_ITEM.ORDER_ID.eq(SALE_ORDER.ID))
                            .and(
                                INVENTORY_UNIT.SERIAL_NUMBER.cast(String::class.java).containsIgnoreCase(it)
                                    .or(INVENTORY_UNIT.BATCH_CODE.cast(String::class.java).containsIgnoreCase(it))
                                    .or(PRODUCT.NAME.cast(String::class.java).containsIgnoreCase(it))
                            )

                    add(DSL.exists(existsSubquery))
                }
            }

        val joinedTables =
            SALE_ORDER
                .leftJoin(CUSTOMER).on(SALE_ORDER.CUSTOMER_ID.eq(CUSTOMER.ID))
                .leftJoin(SALE_ORDER_ITEM).on(SALE_ORDER_ITEM.ORDER_ID.eq(SALE_ORDER.ID))

        val total =
            jooqDsl
                .select(DSL.countDistinct(SALE_ORDER.ID))
                .from(joinedTables)
                .fetchOne(0, Long::class.java) ?: 0L

        val totalAmountField = DSL.sum(SALE_ORDER_ITEM.UNIT_PRICE.mul(SALE_ORDER_ITEM.QUANTITY)).`as`("total_amount")
        val totalQuantityField = DSL.sum(SALE_ORDER_ITEM.QUANTITY).`as`("total_quantity")
        val itemCountField = DSL.countDistinct(SALE_ORDER_ITEM.ID).`as`("item_count")

        val records =
            jooqDsl
                .select(
                    SALE_ORDER.ID,
                    SALE_ORDER.NO,
                    SALE_ORDER.STATUS,
                    SALE_ORDER.ORDER_DATE,
                    SALE_ORDER.CREATED_AT,
                    CUSTOMER.NAME,
                    totalAmountField,
                    totalQuantityField,
                    itemCountField,
                )
                .from(joinedTables)
                .where(conditions)
                .groupBy(
                    SALE_ORDER.ID,
                    SALE_ORDER.NO,
                    SALE_ORDER.STATUS,
                    SALE_ORDER.ORDER_DATE,
                    SALE_ORDER.CREATED_AT,
                    CUSTOMER.NAME,
                )
                .orderBy(SALE_ORDER.CREATED_AT.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize)
                .fetch()
                .map {
                    val status = it[SALE_ORDER.STATUS]
                    val createdAt = it[SALE_ORDER.CREATED_AT]!!

                    val autoCompleteDate: LocalDate? =
                        if (status.name == OrderStatus.PENDING.name) {
                            createdAt.plusDays(7).toLocalDate()
                        } else {
                            null
                        }

                    BasicSaleOrderTO(
                        id = it[SALE_ORDER.ID]!!,
                        orderNo = it[SALE_ORDER.NO]!!,
                        customerName = it[CUSTOMER.NAME] ?: "unknown",
                        status = status.name,
                        orderDate = it[SALE_ORDER.ORDER_DATE]!!,
                        createdAt = it[SALE_ORDER.CREATED_AT]!!.toInstant(),
                        totalAmount = it.get("total_amount", BigDecimal::class.java) ?: BigDecimal.ZERO,
                        totalQuantity = it.get("total_quantity", Long::class.java) ?: 0L,
                        itemCount = it.get("item_count", Int::class.java) ?: 0,
                        autoCompleteDate = autoCompleteDate,
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

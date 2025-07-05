package org.bruwave.abacusflow.usecase.transaction.service.impl

import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCT
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDER
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDER_ITEM
import org.bruwave.abacusflow.generated.jooq.Tables.SUPPLIER
import org.bruwave.abacusflow.generated.jooq.enums.EnumProductType
import org.bruwave.abacusflow.generated.jooq.enums.EnumPurchaseStatus
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
import java.time.LocalDate
import java.util.UUID

@Service
class PurchaseOrderQueryServiceImpl(
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val jooqDsl: DSLContext,
) : PurchaseOrderQueryService {
    override fun listBasicPurchaseOrdersPage(
        pageable: Pageable,
        orderNo: UUID?,
        supplierName: String?,
        status: String?,
        productName: String?,
        orderDate: LocalDate?,
    ): Page<BasicPurchaseOrderTO> {
        val conditions = mutableListOf<Condition>().apply {
            orderNo?.let {
                add(PURCHASE_ORDER.NO.eq(it))
            }

            orderDate?.let {
                add(PURCHASE_ORDER.ORDER_DATE.eq(it))
            }

            supplierName?.takeIf { it.isNotBlank() }?.let {
                add(SUPPLIER.NAME.containsIgnoreCase(it))
            }

            status?.takeIf { it.isNotBlank() }?.let {
                val statusEnum = when (it.uppercase()) {
                    "PENDING"-> EnumPurchaseStatus.PENDING
                    "COMPLETED"-> EnumPurchaseStatus.COMPLETED
                    "CANCELED"-> EnumPurchaseStatus.CANCELED
                    "REVERSED"-> EnumPurchaseStatus.REVERSED
                    else -> throw IllegalArgumentException("Order status not supported: $it")
                }
                add(PURCHASE_ORDER.STATUS.eq(statusEnum))
            }

            productName?.takeIf { it.isNotBlank() }?.let {
                add(PRODUCT.NAME.containsIgnoreCase(it))
            }
        }


        val joinedTables =
            PURCHASE_ORDER
                .leftJoin(SUPPLIER).on(PURCHASE_ORDER.SUPPLIER_ID.eq(SUPPLIER.ID))
                .leftJoin(PURCHASE_ORDER_ITEM).on(PURCHASE_ORDER_ITEM.ORDER_ID.eq(PURCHASE_ORDER.ID))
                .leftJoin(PRODUCT).on(PURCHASE_ORDER_ITEM.PRODUCT_ID.eq(PRODUCT.ID))

        val total =
            jooqDsl
                .select(DSL.countDistinct(PURCHASE_ORDER.ID))
                .from(joinedTables)
                .where(conditions)
                .fetchOne(0, Long::class.java) ?: 0L

        val totalAmountField =
            DSL.sum(PURCHASE_ORDER_ITEM.UNIT_PRICE.mul(PURCHASE_ORDER_ITEM.QUANTITY)).`as`("total_amount")
        val totalQuantityField = DSL.sum(PURCHASE_ORDER_ITEM.QUANTITY).`as`("total_quantity")
        val itemCountField = DSL.countDistinct(PURCHASE_ORDER_ITEM.ID).`as`("item_count")

        val records =
            jooqDsl
                .select(
                    PURCHASE_ORDER.ID,
                    PURCHASE_ORDER.NO,
                    PURCHASE_ORDER.STATUS,
                    PURCHASE_ORDER.CREATED_AT,
                    PURCHASE_ORDER.ORDER_DATE,
                    SUPPLIER.NAME,
                    totalAmountField,
                    totalQuantityField,
                    itemCountField,
                )
                .from(joinedTables)
                .where(conditions)
                .groupBy(
                    PURCHASE_ORDER.ID,
                    PURCHASE_ORDER.NO,
                    PURCHASE_ORDER.STATUS,
                    PURCHASE_ORDER.CREATED_AT,
                    PURCHASE_ORDER.ORDER_DATE,
                    SUPPLIER.NAME,
                )
                .orderBy(PURCHASE_ORDER.CREATED_AT.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize)
                .fetch()
                .map {
                    val status = it[PURCHASE_ORDER.STATUS]!!
                    val createdAt = it[PURCHASE_ORDER.CREATED_AT]!!
                    val autoCompleteDate: LocalDate? =
                        if (status.name == OrderStatus.PENDING.name) {
                            createdAt.plusDays(7).toLocalDate()
                        } else {
                            null
                        }

                    BasicPurchaseOrderTO(
                        id = it[PURCHASE_ORDER.ID]!!,
                        orderNo = it[PURCHASE_ORDER.NO]!!,
                        status = it[PURCHASE_ORDER.STATUS].name,
                        createdAt = it[PURCHASE_ORDER.CREATED_AT]!!.toInstant(),
                        orderDate = it[PURCHASE_ORDER.ORDER_DATE],
                        autoCompleteDate = autoCompleteDate,
                        supplierName = it[SUPPLIER.NAME] ?: "unknown",
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

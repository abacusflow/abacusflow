package org.bruwave.abacusflow.usecase.partner.service.impl

import org.bruwave.abacusflow.db.partner.SupplierRepository
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDERS
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDER_ITEMS
import org.bruwave.abacusflow.generated.jooq.Tables.SUPPLIERS
import org.bruwave.abacusflow.usecase.partner.BasicSupplierTO
import org.bruwave.abacusflow.usecase.partner.SupplierTO
import org.bruwave.abacusflow.usecase.partner.mapper.toTO
import org.bruwave.abacusflow.usecase.partner.service.SupplierQueryService
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.OffsetDateTime

@Service
class SupplierQueryServiceImpl(
    private val supplierRepository: SupplierRepository,
    private val jooqDsl: DSLContext,
) : SupplierQueryService {
    override fun getSupplier(id: Long): SupplierTO =
        supplierRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Supplier not found") }
            .toTO()

    override fun getSupplier(name: String): SupplierTO =
        supplierRepository
            .findByName(name)
            ?.toTO()
            ?: throw NoSuchElementException("Supplier not found")

    override fun listBasicSuppliersPage(
        pageable: Pageable,
        name: String?,
        contactPerson: String?,
        phone: String?,
        address: String?,
    ): Page<BasicSupplierTO> {
        val conditions = mutableListOf<Condition>()

        name?.takeIf { it.isNotBlank() }?.let {
            conditions += SUPPLIERS.NAME.containsIgnoreCase(it)
        }
        contactPerson?.takeIf { it.isNotBlank() }?.let {
            conditions += SUPPLIERS.CONTACT_PERSON.containsIgnoreCase(it)
        }
        phone?.takeIf { it.isNotBlank() }?.let {
            conditions += SUPPLIERS.PHONE.containsIgnoreCase(it)
        }
        address?.takeIf { it.isNotBlank() }?.let {
            conditions += SUPPLIERS.ADDRESS.containsIgnoreCase(it)
        }

        // 1. 查询总记录数
        val total =
            jooqDsl
                .selectCount()
                .from(SUPPLIERS)
                .where(conditions)
                .fetchOne(0, Long::class.java) ?: 0L

        // 2. 查询分页数据（明确字段）
        val records =
            jooqDsl
                .select(
                    SUPPLIERS.ID,
                    SUPPLIERS.NAME,
                    SUPPLIERS.CONTACT_PERSON,
                    SUPPLIERS.PHONE,
                    SUPPLIERS.ADDRESS,
                    // 聚合字段
                    DSL.countDistinct(PURCHASE_ORDERS.ID).`as`("total_order_count"),
                    DSL.sum(
                        PURCHASE_ORDER_ITEMS.UNIT_PRICE
                            .mul(PURCHASE_ORDER_ITEMS.QUANTITY)
                    ).`as`("total_order_amount"),
                    DSL.max(PURCHASE_ORDERS.CREATED_AT).`as`("last_order_time")
                )
                .from(SUPPLIERS)
                .leftJoin(PURCHASE_ORDERS).on(PURCHASE_ORDERS.SUPPLIER_ID.eq(SUPPLIERS.ID))
                .leftJoin(PURCHASE_ORDER_ITEMS).on(PURCHASE_ORDER_ITEMS.ORDER_ID.eq(PURCHASE_ORDERS.ID))
                .where(conditions)
                .groupBy(
                    SUPPLIERS.ID,
                    SUPPLIERS.NAME,
                    SUPPLIERS.CONTACT_PERSON,
                    SUPPLIERS.PHONE,
                    SUPPLIERS.ADDRESS,
                )
                .orderBy(SUPPLIERS.CREATED_AT.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize)
                .fetch()
                .map {
                    BasicSupplierTO(
                        id = it[SUPPLIERS.ID]!!,
                        name = it[SUPPLIERS.NAME]!!,
                        contactPerson = it[SUPPLIERS.CONTACT_PERSON],
                        phone = it[SUPPLIERS.PHONE],
                        address = it[SUPPLIERS.ADDRESS],
                        totalOrderCount = it.get("total_order_count", Int::class.java) ?: 0,
                        totalOrderAmount = it.get("total_order_amount", BigDecimal::class.java) ?: BigDecimal.ZERO,
                        lastOrderTime = it.get("last_order_time", OffsetDateTime::class.java)?.toInstant(),
                    )
                }

        return PageImpl(records, pageable, total)
    }

    override fun listSuppliers(): List<SupplierTO> {
        return supplierRepository
            .findAll()
            .map { it.toTO() }
    }
}

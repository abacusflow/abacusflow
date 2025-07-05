package org.bruwave.abacusflow.usecase.partner.service.impl

import org.bruwave.abacusflow.db.partner.SupplierRepository
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDER
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDER_ITEM
import org.bruwave.abacusflow.generated.jooq.Tables.SUPPLIER
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
        val conditions =
            buildList<Condition> {
                name?.takeIf { it.isNotBlank() }?.let {
                    add(SUPPLIER.NAME.containsIgnoreCase(it))
                }
                contactPerson?.takeIf { it.isNotBlank() }?.let {
                    add(SUPPLIER.CONTACT_PERSON.containsIgnoreCase(it))
                }
                phone?.takeIf { it.isNotBlank() }?.let {
                    add(SUPPLIER.PHONE.containsIgnoreCase(it))
                }
                address?.takeIf { it.isNotBlank() }?.let {
                    add(SUPPLIER.ADDRESS.containsIgnoreCase(it))
                }
            }

        // 1. 查询总记录数
        val total =
            jooqDsl
                .selectCount()
                .from(SUPPLIER)
                .where(conditions)
                .fetchOne(0, Long::class.java) ?: 0L

        // 2. 查询分页数据（明确字段）
        val records =
            jooqDsl
                .select(
                    SUPPLIER.ID,
                    SUPPLIER.NAME,
                    SUPPLIER.CONTACT_PERSON,
                    SUPPLIER.PHONE,
                    SUPPLIER.ADDRESS,
                    // 聚合字段
                    DSL.countDistinct(PURCHASE_ORDER.ID).`as`("total_order_count"),
                    DSL.sum(
                        PURCHASE_ORDER_ITEM.UNIT_PRICE
                            .mul(PURCHASE_ORDER_ITEM.QUANTITY),
                    ).`as`("total_order_amount"),
                    DSL.max(PURCHASE_ORDER.CREATED_AT).`as`("last_order_time"),
                )
                .from(SUPPLIER)
                .leftJoin(PURCHASE_ORDER).on(PURCHASE_ORDER.SUPPLIER_ID.eq(SUPPLIER.ID))
                .leftJoin(PURCHASE_ORDER_ITEM).on(PURCHASE_ORDER_ITEM.ORDER_ID.eq(PURCHASE_ORDER.ID))
                .where(conditions)
                .groupBy(
                    SUPPLIER.ID,
                    SUPPLIER.NAME,
                    SUPPLIER.CONTACT_PERSON,
                    SUPPLIER.PHONE,
                    SUPPLIER.ADDRESS,
                )
                .orderBy(SUPPLIER.CREATED_AT.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize)
                .fetch()
                .map {
                    BasicSupplierTO(
                        id = it[SUPPLIER.ID]!!,
                        name = it[SUPPLIER.NAME]!!,
                        contactPerson = it[SUPPLIER.CONTACT_PERSON],
                        phone = it[SUPPLIER.PHONE],
                        address = it[SUPPLIER.ADDRESS],
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

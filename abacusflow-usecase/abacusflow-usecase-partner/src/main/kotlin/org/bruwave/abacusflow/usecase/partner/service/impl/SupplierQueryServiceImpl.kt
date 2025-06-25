package org.bruwave.abacusflow.usecase.partner.service.impl

import org.bruwave.abacusflow.db.partner.SupplierRepository
import org.bruwave.abacusflow.generated.jooq.Tables.SUPPLIERS
import org.bruwave.abacusflow.usecase.partner.BasicSupplierTO
import org.bruwave.abacusflow.usecase.partner.SupplierTO
import org.bruwave.abacusflow.usecase.partner.mapper.toTO
import org.bruwave.abacusflow.usecase.partner.service.SupplierQueryService
import org.jooq.Condition
import org.jooq.DSLContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

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

    override fun listSuppliersPage(
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
                )
                .from(SUPPLIERS)
                .where(conditions)
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

package org.bruwave.abacusflow.usecase.partner.service.impl

import org.bruwave.abacusflow.db.partner.CustomerRepository
import org.bruwave.abacusflow.generated.jooq.Tables.CUSTOMERS
import org.bruwave.abacusflow.usecase.partner.BasicCustomerTO
import org.bruwave.abacusflow.usecase.partner.CustomerTO
import org.bruwave.abacusflow.usecase.partner.mapper.toTO
import org.bruwave.abacusflow.usecase.partner.service.CustomerQueryService
import org.jooq.Condition
import org.jooq.DSLContext
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class CustomerQueryServiceImpl(
    private val customerRepository: CustomerRepository,
    private val jooqDsl: DSLContext,
) : CustomerQueryService {
    override fun getCustomer(id: Long): CustomerTO =
        customerRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Customer not found") }
            .toTO()

    override fun getCustomer(name: String): CustomerTO =
        customerRepository
            .findByName(name)
            ?.toTO()
            ?: throw NoSuchElementException("Customer not found")

    override fun listBasicCustomersPage(
        pageable: Pageable,
        name: String?,
        phone: String?,
        address: String?,
    ): Page<BasicCustomerTO> {
        val conditions = mutableListOf<Condition>()

        name?.takeIf { it.isNotBlank() }?.let {
            conditions += CUSTOMERS.NAME.containsIgnoreCase(it)
        }

        phone?.takeIf { it.isNotBlank() }?.let {
            conditions += CUSTOMERS.PHONE.containsIgnoreCase(it)
        }

        address?.takeIf { it.isNotBlank() }?.let {
            conditions += CUSTOMERS.ADDRESS.containsIgnoreCase(it)
        }

        // 1. 查询总数
        val total =
            jooqDsl
                .selectCount()
                .from(CUSTOMERS)
                .where(conditions)
                .fetchOne(0, Long::class.java) ?: 0L

        // 2. 查询分页数据，明确列出字段
        val records =
            jooqDsl
                .select(
                    CUSTOMERS.ID,
                    CUSTOMERS.NAME,
                    CUSTOMERS.PHONE,
                    CUSTOMERS.ADDRESS,
                )
                .from(CUSTOMERS)
                .where(conditions)
                .orderBy(CUSTOMERS.CREATED_AT.desc()) // 或 pageable.sort 转换
                .offset(pageable.offset)
                .limit(pageable.pageSize)
                .fetch()
                .map {
                    BasicCustomerTO(
                        id = it[CUSTOMERS.ID]!!,
                        name = it[CUSTOMERS.NAME]!!,
                        phone = it[CUSTOMERS.PHONE],
                        address = it[CUSTOMERS.ADDRESS],
                    )
                }

        return PageImpl(records, pageable, total)
    }

    override fun listCustomers(): List<CustomerTO> {
        return customerRepository
            .findAll()
            .map { it.toTO() }
    }
}

package org.abacusflow.usecase.partner.service

import org.abacusflow.usecase.partner.BasicCustomerTO
import org.abacusflow.usecase.partner.CustomerTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CustomerQueryService {
    fun getCustomer(id: Long): CustomerTO

    fun getCustomer(name: String): CustomerTO

    fun listBasicCustomersPage(
        pageable: Pageable,
        name: String?,
        phone: String?,
        address: String?,
    ): Page<BasicCustomerTO>

    fun listCustomers(): List<CustomerTO>
}

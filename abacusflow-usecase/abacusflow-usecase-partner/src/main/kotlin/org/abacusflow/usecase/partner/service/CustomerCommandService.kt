package org.abacusflow.usecase.partner.service

import org.abacusflow.usecase.partner.CreateCustomerInputTO
import org.abacusflow.usecase.partner.CustomerTO
import org.abacusflow.usecase.partner.UpdateCustomerInputTO

interface CustomerCommandService {
    fun createCustomer(input: CreateCustomerInputTO): CustomerTO

    fun updateCustomer(
        id: Long,
        input: UpdateCustomerInputTO,
    ): CustomerTO

    fun deleteCustomer(id: Long): CustomerTO
}

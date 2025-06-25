package org.bruwave.abacusflow.usecase.partner.service

import org.bruwave.abacusflow.usecase.partner.CreateCustomerInputTO
import org.bruwave.abacusflow.usecase.partner.CustomerTO
import org.bruwave.abacusflow.usecase.partner.UpdateCustomerInputTO

interface CustomerCommandService {
    fun createCustomer(input: CreateCustomerInputTO): CustomerTO

    fun updateCustomer(
        id: Long,
        input: UpdateCustomerInputTO,
    ): CustomerTO

    fun deleteCustomer(id: Long): CustomerTO
}

package org.bruwave.abacusflow.usecase.partner

interface CustomerService {
    fun createCustomer(input: CreateCustomerInputTO): CustomerTO
    fun updateCustomer(id: Long, input: UpdateCustomerInputTO): CustomerTO
    fun deleteCustomer(id: Long): CustomerTO
    fun getCustomer(id: Long): CustomerTO
    fun getCustomer(name: String): CustomerTO
    fun listCustomers(): List<BasicCustomerTO>
} 
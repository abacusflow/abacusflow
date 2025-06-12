package org.bruwave.abacusflow.usecase.partner

interface CustomerService {
    fun createCustomer(customer: CustomerTO): CustomerTO
    fun updateCustomer(customerTO: CustomerTO): CustomerTO
    fun deleteCustomer(customerTO: CustomerTO): CustomerTO
    fun getCustomer(id: Long): CustomerTO
    fun getCustomer(name: String): CustomerTO
    fun listCustomers(): List<CustomerTO>
} 
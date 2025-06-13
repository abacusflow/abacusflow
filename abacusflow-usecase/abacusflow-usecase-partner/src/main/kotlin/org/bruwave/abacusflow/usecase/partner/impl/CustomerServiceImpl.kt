package org.bruwave.abacusflow.usecase.partner.impl

import org.bruwave.abacusflow.db.partner.CustomerRepository
import org.bruwave.abacusflow.partner.Customer
import org.bruwave.abacusflow.usecase.partner.CustomerService
import org.bruwave.abacusflow.usecase.partner.CustomerTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
) : CustomerService {
    override fun createCustomer(customer: CustomerTO): CustomerTO {
        val newCustomer = Customer(
            name = customer.name,
            phone = customer.phone
        )
        customer.address?.let { newCustomer.updateContactInfo(null, it, null) }
        return customerRepository.save(newCustomer).toCustomerTO()
    }

    override fun updateCustomer(customerTO: CustomerTO): CustomerTO {
        val customer = customerRepository.findById(customerTO.id)
            .orElseThrow { NoSuchElementException("Customer not found") }
        customer.updateContactInfo(
            newName = customerTO.name,
            newAddress = customerTO.address,
            newPhone = customerTO.phone
        )
        return customerRepository.save(customer).toCustomerTO()
    }

    override fun deleteCustomer(customerTO: CustomerTO): CustomerTO {
        val customer = customerRepository.findById(customerTO.id)
            .orElseThrow { NoSuchElementException("Customer not found") }
        customerRepository.delete(customer)
        return customerTO
    }

    override fun getCustomer(id: Long): CustomerTO {
        return customerRepository.findById(id)
            .orElseThrow { NoSuchElementException("Customer not found") }
            .toCustomerTO()
    }

    override fun getCustomer(name: String): CustomerTO {
        return customerRepository.findByName(name)
            ?.toCustomerTO()
            ?: throw NoSuchElementException("Customer not found")
    }

    override fun listCustomers(): List<CustomerTO> {
        return customerRepository.findAll().map { it.toCustomerTO() }
    }

    private fun Customer.toCustomerTO() = CustomerTO(
        id = id,
        name = name,
        phone = phone,
        address = address,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
} 
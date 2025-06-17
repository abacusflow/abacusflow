package org.bruwave.abacusflow.usecase.partner.impl

import org.bruwave.abacusflow.db.partner.CustomerRepository
import org.bruwave.abacusflow.partner.Customer
import org.bruwave.abacusflow.usecase.partner.BasicCustomerTO
import org.bruwave.abacusflow.usecase.partner.CreateCustomerInputTO
import org.bruwave.abacusflow.usecase.partner.CustomerService
import org.bruwave.abacusflow.usecase.partner.CustomerTO
import org.bruwave.abacusflow.usecase.partner.UpdateCustomerInputTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerServiceImpl(
    private val customerRepository: CustomerRepository,
) : CustomerService {
    override fun createCustomer(input: CreateCustomerInputTO): CustomerTO {
        val newCustomer =
            Customer(
                name = input.name,
                phone = input.phone,
                address = input.address,
            )
        return customerRepository.save(newCustomer).toTO()
    }

    override fun updateCustomer(
        id: Long,
        input: UpdateCustomerInputTO,
    ): CustomerTO {
        val customer =
            customerRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Customer not found") }
        customer.updateContactInfo(
            newName = input.name,
            newAddress = input.address,
            newPhone = input.phone,
        )
        return customerRepository.save(customer).toTO()
    }

    override fun deleteCustomer(id: Long): CustomerTO {
        val customer =
            customerRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Customer not found") }
        customerRepository.delete(customer)
        return customer.toTO()
    }

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

    override fun listCustomers(): List<BasicCustomerTO> = customerRepository.findAll().map { it.toBasicTO() }

    private fun Customer.toTO() =
        CustomerTO(
            id = id,
            name = name,
            phone = phone,
            address = address,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun Customer.toBasicTO() =
        BasicCustomerTO(
            id = id,
            name = name,
            phone = phone,
            address = address,
        )
}

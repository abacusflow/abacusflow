package org.abacusflow.usecase.partner.service.impl

import org.abacusflow.db.partner.CustomerRepository
import org.abacusflow.partner.Customer
import org.abacusflow.usecase.partner.CreateCustomerInputTO
import org.abacusflow.usecase.partner.CustomerTO
import org.abacusflow.usecase.partner.UpdateCustomerInputTO
import org.abacusflow.usecase.partner.mapper.toTO
import org.abacusflow.usecase.partner.service.CustomerCommandService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerCommandServiceImpl(
    private val customerRepository: CustomerRepository,
) : CustomerCommandService {
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
}

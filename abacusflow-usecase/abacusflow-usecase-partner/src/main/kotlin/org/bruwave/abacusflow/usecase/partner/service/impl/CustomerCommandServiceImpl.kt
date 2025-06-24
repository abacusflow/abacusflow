package org.bruwave.abacusflow.usecase.partner.service.impl

import org.bruwave.abacusflow.db.partner.CustomerRepository
import org.bruwave.abacusflow.partner.Customer
import org.bruwave.abacusflow.usecase.partner.BasicCustomerTO
import org.bruwave.abacusflow.usecase.partner.CreateCustomerInputTO
import org.bruwave.abacusflow.usecase.partner.service.CustomerCommandService
import org.bruwave.abacusflow.usecase.partner.CustomerTO
import org.bruwave.abacusflow.usecase.partner.UpdateCustomerInputTO
import org.bruwave.abacusflow.usecase.partner.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.partner.mapper.toTO
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

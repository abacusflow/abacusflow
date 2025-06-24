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
import org.bruwave.abacusflow.usecase.partner.service.CustomerQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerQueryServiceImpl(
    private val customerRepository: CustomerRepository,
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

    override fun listCustomers(): List<BasicCustomerTO> = customerRepository.findAll().map { it.toBasicTO() }
}

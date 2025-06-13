package org.bruwave.abacusflow.portal.web.partner

import org.bruwave.abacusflow.portal.web.api.CustomersApi
import org.bruwave.abacusflow.portal.web.model.BasicCustomerVO
import org.bruwave.abacusflow.portal.web.model.CreateCustomerInputVO
import org.bruwave.abacusflow.portal.web.model.CustomerVO
import org.bruwave.abacusflow.portal.web.model.UpdateCustomerInputVO
import org.bruwave.abacusflow.usecase.partner.CustomerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerController(
    private val customerService: CustomerService,
): CustomersApi{
    override fun listCustomers(): ResponseEntity<List<BasicCustomerVO>> {
        return super.listCustomers()
    }

    override fun getCustomer(id: Long): ResponseEntity<CustomerVO> {
        return super.getCustomer(id)
    }

    override fun addCustomer(createCustomerInputVO: CreateCustomerInputVO): ResponseEntity<CustomerVO> {
        return super.addCustomer(createCustomerInputVO)
    }

    override fun updateCustomer(id: Long, updateCustomerInputVO: UpdateCustomerInputVO): ResponseEntity<CustomerVO> {
        return super.updateCustomer(id, updateCustomerInputVO)
    }

    override fun deleteCustomer(id: Long): ResponseEntity<CustomerVO> {
        return super.deleteCustomer(id)
    }
}
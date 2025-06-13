package org.bruwave.abacusflow.portal.web.partner

import org.bruwave.abacusflow.portal.web.api.CustomersApi
import org.bruwave.abacusflow.portal.web.model.BasicUserVO
import org.bruwave.abacusflow.portal.web.model.CustomerVO
import org.bruwave.abacusflow.usecase.partner.CustomerService
import org.bruwave.abacusflow.usecase.partner.CustomerTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

@RestController
class CustomerController(
    private val customerService: CustomerService,
): CustomersApi{
    override fun listCustomers(): ResponseEntity<List<CustomerVO>> {
        return super.listCustomers()
    }

    override fun getCustomer(id: Long): ResponseEntity<CustomerVO> {
        return super.getCustomer(id)
    }

    override fun addCustomer(customerVO: CustomerVO): ResponseEntity<CustomerVO> {
        return super.addCustomer(customerVO)
    }

    override fun updateCustomer(id: Long, customerVO: CustomerVO): ResponseEntity<CustomerVO> {
        return super.updateCustomer(id, customerVO)
    }

    override fun deleteCustomer(id: Long): ResponseEntity<CustomerVO> {
        return super.deleteCustomer(id)
    }
}
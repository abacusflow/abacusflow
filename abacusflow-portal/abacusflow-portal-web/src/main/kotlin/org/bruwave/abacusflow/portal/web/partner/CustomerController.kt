package org.bruwave.abacusflow.portal.web.partner

import org.bruwave.abacusflow.portal.web.api.CustomersApi
import org.bruwave.abacusflow.portal.web.model.BasicCustomerVO
import org.bruwave.abacusflow.portal.web.model.CreateCustomerInputVO
import org.bruwave.abacusflow.portal.web.model.CustomerVO
import org.bruwave.abacusflow.portal.web.model.UpdateCustomerInputVO
import org.bruwave.abacusflow.usecase.partner.CreateCustomerInputTO
import org.bruwave.abacusflow.usecase.partner.CustomerService
import org.bruwave.abacusflow.usecase.partner.UpdateCustomerInputTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerController(
    private val customerService: CustomerService
) : CustomersApi {

    override fun listCustomers(): ResponseEntity<List<BasicCustomerVO>> {
        val customerVOs = customerService.listCustomers().map { customer ->
            customer.toBasicVO()
        }
        return ResponseEntity.ok(customerVOs)
    }

    override fun getCustomer(id: Long): ResponseEntity<CustomerVO> {
        val customer = customerService.getCustomer(id)
        return ResponseEntity.ok(
            customer.toVO()
        )
    }

    override fun addCustomer(createCustomerInputVO: CreateCustomerInputVO): ResponseEntity<CustomerVO> {
        val customer = customerService.createCustomer(
            CreateCustomerInputTO(
                name = createCustomerInputVO.name,
                phone = createCustomerInputVO.phone,
                address = createCustomerInputVO.address,
            )
        )
        return ResponseEntity.ok(
            customer.toVO()

        )
    }

    override fun updateCustomer(
        id: Long,
        updateCustomerInputVO: UpdateCustomerInputVO
    ): ResponseEntity<CustomerVO> {
        val customer = customerService.updateCustomer(
            id,
            UpdateCustomerInputTO(
                name = updateCustomerInputVO.name,
                phone = updateCustomerInputVO.phone,
                address = updateCustomerInputVO.address
            )
        )
        return ResponseEntity.ok(
            customer.toVO()
        )
    }

    override fun deleteCustomer(id: Long): ResponseEntity<CustomerVO> {
        customerService.deleteCustomer(id)
        return ResponseEntity.ok().build()
    }
}
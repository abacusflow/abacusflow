package org.bruwave.abacusflow.portal.web.partner

import org.bruwave.abacusflow.portal.web.api.CustomersApi
import org.bruwave.abacusflow.portal.web.inventory.toBasicVO
import org.bruwave.abacusflow.portal.web.model.BasicCustomerVO
import org.bruwave.abacusflow.portal.web.model.CreateCustomerInputVO
import org.bruwave.abacusflow.portal.web.model.CustomerVO
import org.bruwave.abacusflow.portal.web.model.ListCustomersPage200ResponseVO
import org.bruwave.abacusflow.portal.web.model.ListInventoriesPage200ResponseVO
import org.bruwave.abacusflow.portal.web.model.UpdateCustomerInputVO
import org.bruwave.abacusflow.usecase.partner.CreateCustomerInputTO
import org.bruwave.abacusflow.usecase.partner.service.CustomerCommandService
import org.bruwave.abacusflow.usecase.partner.UpdateCustomerInputTO
import org.bruwave.abacusflow.usecase.partner.service.CustomerQueryService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerController(
    private val customerCommandService: CustomerCommandService,
    private val customerQueryService: CustomerQueryService,
) : CustomersApi {
    override fun listCustomersPage(
        pageIndex: Int,
        pageSize: Int,
        name: String?,
        phone: String?,
        address: String?
    ): ResponseEntity<ListCustomersPage200ResponseVO> {
        val pageable = PageRequest.of(pageIndex - 1, pageSize)

        val page = customerQueryService.listCustomersPage(
            pageable,
            name= name,
            phone= phone,
            address= address,
        ).map { it.toBasicVO() }

        val pageVO = ListCustomersPage200ResponseVO(
            content = page.content,
            totalElements = page.totalElements,
            number = page.number,
            propertySize = page.size
        )

        return ResponseEntity.ok(pageVO)
    }

    override fun getCustomer(id: Long): ResponseEntity<CustomerVO> {
        val customer = customerQueryService.getCustomer(id)
        return ResponseEntity.ok(
            customer.toVO(),
        )
    }

    override fun addCustomer(createCustomerInputVO: CreateCustomerInputVO): ResponseEntity<CustomerVO> {
        val customer =
            customerCommandService.createCustomer(
                CreateCustomerInputTO(
                    name = createCustomerInputVO.name,
                    phone = createCustomerInputVO.phone,
                    address = createCustomerInputVO.address,
                ),
            )
        return ResponseEntity.ok(
            customer.toVO(),
        )
    }

    override fun updateCustomer(
        id: Long,
        updateCustomerInputVO: UpdateCustomerInputVO,
    ): ResponseEntity<CustomerVO> {
        val customer =
            customerCommandService.updateCustomer(
                id,
                UpdateCustomerInputTO(
                    name = updateCustomerInputVO.name,
                    phone = updateCustomerInputVO.phone,
                    address = updateCustomerInputVO.address,
                ),
            )
        return ResponseEntity.ok(
            customer.toVO(),
        )
    }

    override fun deleteCustomer(id: Long): ResponseEntity<Unit> {
        customerCommandService.deleteCustomer(id)
        return ResponseEntity.ok().build()
    }
}

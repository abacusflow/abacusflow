package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.usecase.partner.CustomerService
import org.bruwave.abacusflow.usecase.partner.CustomerTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController(private val customerService: CustomerService) {

    @GetMapping
    fun listCustomers(): List<CustomerTO> = customerService.listCustomers()

    @PostMapping
    fun createCustomer(@RequestBody customer: CustomerTO): CustomerTO = customerService.createCustomer(customer)

    @GetMapping("/{id}")
    fun getCustomer(@PathVariable id: Long): CustomerTO = customerService.getCustomer(id)

    @PutMapping("/{id}")
    fun updateCustomer(@PathVariable id: Long, @RequestBody customerTO: CustomerTO): CustomerTO {
        return customerService.updateCustomer(customerTO.copy(id = id))
    }

    @DeleteMapping("/{id}")
    fun deleteCustomer(@PathVariable id: Long): CustomerTO {
        return customerService.deleteCustomer(CustomerTO(id = id, name = "", address = "", createdAt = java.time.Instant.now(), updatedAt = java.time.Instant.now()))
    }
} 
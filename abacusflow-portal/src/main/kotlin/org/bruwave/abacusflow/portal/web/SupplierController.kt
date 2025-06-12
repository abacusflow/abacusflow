package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.usecase.partner.SupplierService
import org.bruwave.abacusflow.usecase.partner.SupplierTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/suppliers")
class SupplierController(private val supplierService: SupplierService) {

    @GetMapping
    fun listSuppliers(): List<SupplierTO> = supplierService.listSuppliers()

    @PostMapping
    fun createSupplier(@RequestBody supplier: SupplierTO): SupplierTO = supplierService.createSupplier(supplier)

    @GetMapping("/{id}")
    fun getSupplier(@PathVariable id: Long): SupplierTO = supplierService.getSupplier(id)

    @PutMapping("/{id}")
    fun updateSupplier(@PathVariable id: Long, @RequestBody supplierTO: SupplierTO): SupplierTO {
        return supplierService.updateSupplier(supplierTO.copy(id = id))
    }

    @DeleteMapping("/{id}")
    fun deleteSupplier(@PathVariable id: Long): SupplierTO {
        return supplierService.deleteSupplier(SupplierTO(id = id, name = "", contactPerson = null, phone = null, createdAt = java.time.Instant.now(), updatedAt = java.time.Instant.now()))
    }
} 
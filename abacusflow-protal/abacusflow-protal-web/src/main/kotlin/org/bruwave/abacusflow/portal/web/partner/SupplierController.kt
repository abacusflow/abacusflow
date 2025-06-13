package org.bruwave.abacusflow.portal.web.partner

import org.bruwave.abacusflow.portal.web.api.SuppliersApi
import org.bruwave.abacusflow.portal.web.model.SupplierVO
import org.bruwave.abacusflow.usecase.partner.SupplierService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SupplierController(
    private val supplierService: SupplierService
) : SuppliersApi {
    override fun listSuppliers(): ResponseEntity<List<SupplierVO>> {
        return super.listSuppliers()
    }

    override fun getSupplier(id: Long): ResponseEntity<SupplierVO> {
        return super.getSupplier(id)
    }

    override fun addSupplier(supplierVO: SupplierVO): ResponseEntity<SupplierVO> {
        return super.addSupplier(supplierVO)
    }

    override fun updateSupplier(id: Long, supplierVO: SupplierVO): ResponseEntity<SupplierVO> {
        return super.updateSupplier(id, supplierVO)
    }

    override fun deleteSupplier(id: Long): ResponseEntity<SupplierVO> {
        return super.deleteSupplier(id)
    }
}
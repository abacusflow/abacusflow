package org.bruwave.abacusflow.portal.web.partner

import org.bruwave.abacusflow.portal.web.api.SuppliersApi
import org.bruwave.abacusflow.portal.web.model.BasicSupplierVO
import org.bruwave.abacusflow.portal.web.model.CreateSupplierInputVO
import org.bruwave.abacusflow.portal.web.model.SupplierVO
import org.bruwave.abacusflow.portal.web.model.UpdateSupplierInputVO
import org.bruwave.abacusflow.usecase.partner.SupplierService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SupplierController(
    private val supplierService: SupplierService
) : SuppliersApi {
    override fun listSuppliers(): ResponseEntity<List<BasicSupplierVO>> {
        return super.listSuppliers()
    }

    override fun getSupplier(id: Long): ResponseEntity<SupplierVO> {
        return super.getSupplier(id)
    }

    override fun addSupplier(createSupplierInputVO: CreateSupplierInputVO): ResponseEntity<SupplierVO> {
        return super.addSupplier(createSupplierInputVO)
    }

    override fun updateSupplier(id: Long, updateSupplierInputVO: UpdateSupplierInputVO): ResponseEntity<SupplierVO> {
        return super.updateSupplier(id, updateSupplierInputVO)
    }

    override fun deleteSupplier(id: Long): ResponseEntity<SupplierVO> {
        return super.deleteSupplier(id)
    }
}
package org.bruwave.abacusflow.portal.web.partner

import org.bruwave.abacusflow.portal.web.api.SuppliersApi
import org.bruwave.abacusflow.portal.web.model.BasicSupplierVO
import org.bruwave.abacusflow.portal.web.model.CreateSupplierInputVO
import org.bruwave.abacusflow.portal.web.model.SupplierVO
import org.bruwave.abacusflow.portal.web.model.UpdateSupplierInputVO
import org.bruwave.abacusflow.usecase.partner.CreateSupplierInputTO
import org.bruwave.abacusflow.usecase.partner.SupplierService
import org.bruwave.abacusflow.usecase.partner.UpdateSupplierInputTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SupplierController(
    private val supplierService: SupplierService
) : SuppliersApi {

    override fun listSuppliers(): ResponseEntity<List<BasicSupplierVO>> {
        val suppliers = supplierService.listSuppliers()
        val supplierVOs = suppliers.map { supplier ->
            supplier.toBasicVO()
        }
        return ResponseEntity.ok(supplierVOs)
    }

    override fun getSupplier(id: Long): ResponseEntity<SupplierVO> {
        val supplier = supplierService.getSupplier(id)
        return ResponseEntity.ok(
            supplier.toVO()
        )
    }

    override fun addSupplier(createSupplierInputVO: CreateSupplierInputVO): ResponseEntity<SupplierVO> {
        val supplier = supplierService.createSupplier(
            CreateSupplierInputTO(
                createSupplierInputVO.name,
                createSupplierInputVO.contactPerson,
                createSupplierInputVO.phone
            )
        )
        return ResponseEntity.ok(
            supplier.toVO()
        )
    }

    override fun updateSupplier(
        id: Long,
        updateSupplierInputVO: UpdateSupplierInputVO
    ): ResponseEntity<SupplierVO> {
        val supplier = supplierService.updateSupplier(
            id,
            UpdateSupplierInputTO(
                name = updateSupplierInputVO.name,
                contactPerson = updateSupplierInputVO.contactPerson,
                phone = updateSupplierInputVO.phone
            )
        )
        return ResponseEntity.ok(
            supplier.toVO()
        )
    }

    override fun deleteSupplier(id: Long): ResponseEntity<Unit> {
        supplierService.deleteSupplier(id)
        return ResponseEntity.ok().build()
    }
}
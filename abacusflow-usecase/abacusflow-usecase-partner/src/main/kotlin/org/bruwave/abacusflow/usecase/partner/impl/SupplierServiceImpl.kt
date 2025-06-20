package org.bruwave.abacusflow.usecase.partner.impl

import org.bruwave.abacusflow.db.partner.SupplierRepository
import org.bruwave.abacusflow.partner.Supplier
import org.bruwave.abacusflow.usecase.partner.BasicSupplierTO
import org.bruwave.abacusflow.usecase.partner.CreateSupplierInputTO
import org.bruwave.abacusflow.usecase.partner.SupplierService
import org.bruwave.abacusflow.usecase.partner.SupplierTO
import org.bruwave.abacusflow.usecase.partner.UpdateSupplierInputTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SupplierServiceImpl(
    private val supplierRepository: SupplierRepository,
) : SupplierService {
    override fun createSupplier(supplier: CreateSupplierInputTO): SupplierTO {
        val newSupplier =
            Supplier(
                name = supplier.name,
                phone = supplier.phone,
                contactPerson = supplier.contactPerson,
                address = supplier.address,
            )
        return supplierRepository.save(newSupplier).toTO()
    }

    override fun updateSupplier(
        id: Long,
        supplierTO: UpdateSupplierInputTO,
    ): SupplierTO {
        val supplier =
            supplierRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Supplier not found") }
        supplier.updateContactInfo(
            newName = supplierTO.name,
            newContactPerson = supplierTO.contactPerson,
            newPhone = supplierTO.phone,
            newAddress = supplierTO.address,
        )
        return supplierRepository.save(supplier).toTO()
    }

    override fun deleteSupplier(id: Long): SupplierTO {
        val supplier =
            supplierRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Supplier not found") }
        supplierRepository.delete(supplier)
        return supplier.toTO()
    }

    override fun getSupplier(id: Long): SupplierTO =
        supplierRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Supplier not found") }
            .toTO()

    override fun getSupplier(name: String): SupplierTO =
        supplierRepository
            .findByName(name)
            ?.toTO()
            ?: throw NoSuchElementException("Supplier not found")

    override fun listSuppliers(): List<BasicSupplierTO> = supplierRepository.findAll().map { it.toBasicTO() }

    private fun Supplier.toTO() =
        SupplierTO(
            id = id,
            name = name,
            contactPerson = contactPerson,
            phone = phone,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

    private fun Supplier.toBasicTO() =
        BasicSupplierTO(
            id = id,
            name = name,
            contactPerson = contactPerson,
            phone = phone,
            address = address,
        )
}

package org.bruwave.abacusflow.usecase.partner.impl

import org.bruwave.abacusflow.db.SupplierRepository
import org.bruwave.abacusflow.partner.Supplier
import org.bruwave.abacusflow.usecase.partner.SupplierService
import org.bruwave.abacusflow.usecase.partner.SupplierTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SupplierServiceImpl(
    private val supplierRepository: SupplierRepository,
) : SupplierService {
    override fun createSupplier(supplier: SupplierTO): SupplierTO {
        val newSupplier = Supplier(
            name = supplier.name,
            phone = supplier.phone
        )
        supplier.contactPerson?.let { newSupplier.updateContactInfo(null, it, null) }
        return supplierRepository.save(newSupplier).toSupplierTO()
    }

    override fun updateSupplier(supplierTO: SupplierTO): SupplierTO {
        val supplier = supplierRepository.findById(supplierTO.id)
            .orElseThrow { NoSuchElementException("Supplier not found") }
        supplier.updateContactInfo(
            newName = supplierTO.name,
            newContactPerson = supplierTO.contactPerson,
            newPhone = supplierTO.phone
        )
        return supplierRepository.save(supplier).toSupplierTO()
    }

    override fun deleteSupplier(supplierTO: SupplierTO): SupplierTO {
        val supplier = supplierRepository.findById(supplierTO.id)
            .orElseThrow { NoSuchElementException("Supplier not found") }
        supplierRepository.delete(supplier)
        return supplierTO
    }

    override fun getSupplier(id: Long): SupplierTO {
        return supplierRepository.findById(id)
            .orElseThrow { NoSuchElementException("Supplier not found") }
            .toSupplierTO()
    }

    override fun getSupplier(name: String): SupplierTO {
        return supplierRepository.findByName(name)
            ?.toSupplierTO()
            ?: throw NoSuchElementException("Supplier not found")
    }

    override fun listSuppliers(): List<SupplierTO> {
        return supplierRepository.findAll().map { it.toSupplierTO() }
    }

    private fun Supplier.toSupplierTO() = SupplierTO(
        id = id,
        name = name,
        contactPerson = contactPerson,
        phone = phone,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
} 
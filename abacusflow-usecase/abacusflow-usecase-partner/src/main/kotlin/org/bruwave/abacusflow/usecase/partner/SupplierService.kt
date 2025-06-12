package org.bruwave.abacusflow.usecase.partner

interface SupplierService {
    fun createSupplier(supplier: SupplierTO): SupplierTO
    fun updateSupplier(supplierTO: SupplierTO): SupplierTO
    fun deleteSupplier(supplierTO: SupplierTO): SupplierTO
    fun getSupplier(id: Long): SupplierTO
    fun getSupplier(name: String): SupplierTO
    fun listSuppliers(): List<SupplierTO>
} 
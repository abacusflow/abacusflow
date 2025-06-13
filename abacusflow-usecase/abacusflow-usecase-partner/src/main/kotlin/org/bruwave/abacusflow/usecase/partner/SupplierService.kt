package org.bruwave.abacusflow.usecase.partner

interface SupplierService {
    fun createSupplier(supplier: CreateSupplierInputTO): SupplierTO
    fun updateSupplier(id: Long, supplierTO: UpdateSupplierInputTO): SupplierTO
    fun deleteSupplier(id: Long): SupplierTO
    fun getSupplier(id: Long): SupplierTO
    fun getSupplier(name: String): SupplierTO
    fun listSuppliers(): List<BasicSupplierTO>
} 
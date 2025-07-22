package org.abacusflow.usecase.partner.service

import org.abacusflow.usecase.partner.CreateSupplierInputTO
import org.abacusflow.usecase.partner.SupplierTO
import org.abacusflow.usecase.partner.UpdateSupplierInputTO

interface SupplierCommandService {
    fun createSupplier(supplier: CreateSupplierInputTO): SupplierTO

    fun updateSupplier(
        id: Long,
        supplierTO: UpdateSupplierInputTO,
    ): SupplierTO

    fun deleteSupplier(id: Long): SupplierTO
}

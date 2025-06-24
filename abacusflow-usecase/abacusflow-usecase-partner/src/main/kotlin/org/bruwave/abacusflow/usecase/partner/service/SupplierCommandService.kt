package org.bruwave.abacusflow.usecase.partner.service

import org.bruwave.abacusflow.usecase.partner.BasicSupplierTO
import org.bruwave.abacusflow.usecase.partner.CreateSupplierInputTO
import org.bruwave.abacusflow.usecase.partner.SupplierTO
import org.bruwave.abacusflow.usecase.partner.UpdateSupplierInputTO

interface SupplierCommandService {
    fun createSupplier(supplier: CreateSupplierInputTO): SupplierTO

    fun updateSupplier(
        id: Long,
        supplierTO: UpdateSupplierInputTO,
    ): SupplierTO

    fun deleteSupplier(id: Long): SupplierTO
}
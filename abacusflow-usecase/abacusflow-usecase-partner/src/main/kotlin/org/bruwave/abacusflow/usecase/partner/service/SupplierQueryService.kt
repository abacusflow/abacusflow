package org.bruwave.abacusflow.usecase.partner.service

import org.bruwave.abacusflow.usecase.partner.BasicSupplierTO
import org.bruwave.abacusflow.usecase.partner.CreateSupplierInputTO
import org.bruwave.abacusflow.usecase.partner.SupplierTO
import org.bruwave.abacusflow.usecase.partner.UpdateSupplierInputTO

interface SupplierQueryService {
    fun getSupplier(id: Long): SupplierTO

    fun getSupplier(name: String): SupplierTO

    fun listSuppliers(): List<BasicSupplierTO>
}
package org.abacusflow.usecase.partner.service

import org.abacusflow.usecase.partner.BasicSupplierTO
import org.abacusflow.usecase.partner.SupplierTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SupplierQueryService {
    fun getSupplier(id: Long): SupplierTO

    fun getSupplier(name: String): SupplierTO

    fun listBasicSuppliersPage(
        pageable: Pageable,
        name: String?,
        contactPerson: String?,
        phone: String?,
        address: String?,
    ): Page<BasicSupplierTO>

    fun listSuppliers(): List<SupplierTO>
}

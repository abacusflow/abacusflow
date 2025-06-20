package org.bruwave.abacusflow.usecase.partner

data class CreateSupplierInputTO(
    val name: String,
    val contactPerson: String?,
    val phone: String?,
    val address: String?,
)

data class UpdateSupplierInputTO(
    val name: String?,
    val contactPerson: String?,
    val phone: String?,
    val address: String?,
)

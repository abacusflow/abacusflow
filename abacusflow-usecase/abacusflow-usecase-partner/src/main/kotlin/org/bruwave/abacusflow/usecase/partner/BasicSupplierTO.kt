package org.bruwave.abacusflow.usecase.partner

data class BasicSupplierTO(
    val id: Long,
    val name: String,
    val contactPerson: String?,
    val phone: String?,
    val address: String?,
)

package org.bruwave.abacusflow.usecase.partner

data class BasicCustomerTO(
    val id: Long,
    val name: String,
    val phone: String?,
    val address: String?,
)
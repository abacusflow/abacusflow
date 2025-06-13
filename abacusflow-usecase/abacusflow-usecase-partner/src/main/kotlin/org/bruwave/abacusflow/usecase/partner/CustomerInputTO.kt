package org.bruwave.abacusflow.usecase.partner

data class CreateCustomerInputTO(
    val name: String,
    val address: String,
    val phone: String
)

data class UpdateCustomerInputTO(
    val name: String,
    val phone: String,
    val address: String
)
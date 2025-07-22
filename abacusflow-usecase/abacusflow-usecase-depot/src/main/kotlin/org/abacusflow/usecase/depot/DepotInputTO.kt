package org.abacusflow.usecase.depot

data class CreateDepotInputTO(
    val name: String,
    val location: String?,
    val capacity: Int?,
)

data class UpdateDepotInputTO(
    val name: String?,
    val location: String?,
    val capacity: Int?,
)

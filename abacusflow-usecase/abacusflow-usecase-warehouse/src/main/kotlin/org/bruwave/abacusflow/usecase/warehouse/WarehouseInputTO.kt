package org.bruwave.abacusflow.usecase.warehouse

data class CreateWarehouseInputTO(
    val name: String,
    val location: String?,
    val capacity: Int?
)

data class UpdateWarehouseInputTO(
    val name: String?,
    val location: String?,
    val capacity: Int?
)
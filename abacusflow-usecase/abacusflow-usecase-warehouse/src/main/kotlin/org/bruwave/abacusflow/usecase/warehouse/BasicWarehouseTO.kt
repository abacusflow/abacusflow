package org.bruwave.abacusflow.usecase.warehouse

data class BasicWarehouseTO(
    val id: Long,
    val name: String,
    val location: String?,
    val capacity: Int,
    val enabled: Boolean,
)

package org.bruwave.abacusflow.usecase.inventory

data class BasicInventoryTO(
    val id: Long,
    val productName: String,
    val warehouseName: String,
    val quantity: Int
)

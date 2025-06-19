package org.bruwave.abacusflow.usecase.inventory

data class BasicInventoryTO(
    val id: Long,
    val productName: String,
    val depotName: String,
    val quantity: Long,
    val availableQuantity: Long,
    val expectedQuantity: Long,
    val safetyStock: Long,
    val maxStock: Long,
)

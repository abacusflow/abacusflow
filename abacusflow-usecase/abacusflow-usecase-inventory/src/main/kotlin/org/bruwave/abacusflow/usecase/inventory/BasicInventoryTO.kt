package org.bruwave.abacusflow.usecase.inventory

data class BasicInventoryTO(
    val id: Long,
    val productName: String,
    val quantity: Long,
    val safetyStock: Long,
    val maxStock: Long,
    val units: List<BasicInventoryUnitTO>
)

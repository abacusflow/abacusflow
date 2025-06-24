package org.bruwave.abacusflow.usecase.inventory

data class BasicInventoryTO(
    val id: Long,
    val productName: String,
    val productType: String,
    val quantity: Long,
    val depotNames: List<String>,
    val safetyStock: Long,
    val maxStock: Long,
    val units: List<BasicInventoryUnitTO>
)

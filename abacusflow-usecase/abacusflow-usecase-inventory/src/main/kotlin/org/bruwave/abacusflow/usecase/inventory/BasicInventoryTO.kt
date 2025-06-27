package org.bruwave.abacusflow.usecase.inventory

data class BasicInventoryTO(
    val id: Long,
    val productName: String,
    val productSpecification: String?,
    val productNote: String?,
    val productType: String,
    val initialQuantity: Long,
    val quantity: Long,
    val remainingQuantity: Long,
    val depotNames: List<String>,
    val safetyStock: Long,
    val maxStock: Long,
    val units: List<BasicInventoryUnitTO>,
)

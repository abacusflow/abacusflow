package org.abacusflow.usecase.inventory

import java.time.Instant

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

data class InventoryTO(
    val id: Long,
    val productId: Long,
    val safetyStock: Long,
    val maxStock: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
)

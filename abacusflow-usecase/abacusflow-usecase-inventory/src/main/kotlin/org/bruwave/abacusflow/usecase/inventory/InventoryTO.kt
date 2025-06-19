package org.bruwave.abacusflow.usecase.inventory

import java.time.Instant

data class InventoryTO(
    val id: Long,
    val productId: Long,
    val depotId: Long?,
    val quantity: Long,
    val reservedQuantity: Long,
    val availableQuantity: Long,
    val safetyStock: Long,
    val maxStock: Long,
    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
)

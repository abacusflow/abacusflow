package org.bruwave.abacusflow.usecase.inventory

import org.bruwave.abacusflow.inventory.Inventory
import java.time.Instant

data class InventoryTO(
    val id: Long,
    val productId: Long,
    val warehouseId: Long?,
    val quantity: Int,
    val reservedQuantity: Int,
    val availableQuantity: Int,
    val safetyStock: Int,
    val maxStock: Int,
    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant
)
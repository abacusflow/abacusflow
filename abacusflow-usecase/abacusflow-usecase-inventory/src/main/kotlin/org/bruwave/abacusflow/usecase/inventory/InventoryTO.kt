package org.bruwave.abacusflow.usecase.inventory

import java.time.Instant

data class InventoryTO(
    val id: Long,
    val productId: Long,
    val warehouseId: Long,
    val quantity: Int,
    val safetyStock: Int,
    val version: Long,
    val createdAt: Instant,
    val updatedAt: Instant
) 
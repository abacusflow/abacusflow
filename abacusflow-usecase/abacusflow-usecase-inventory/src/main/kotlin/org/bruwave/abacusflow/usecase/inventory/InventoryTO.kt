package org.bruwave.abacusflow.usecase.inventory

import java.time.Instant

data class InventoryTO(
    val id: Long,
    val productId: Long,
    val safetyStock: Long,
    val maxStock: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
)

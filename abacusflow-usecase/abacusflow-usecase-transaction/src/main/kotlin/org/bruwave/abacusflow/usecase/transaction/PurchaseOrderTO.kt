package org.bruwave.abacusflow.usecase.transaction

import java.time.Instant

data class PurchaseOrderTO(
    val id: Long,
    val supplierId: Long,
    val status: String,
    val items: List<PurchaseItemTO>,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class PurchaseItemTO(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double
) 
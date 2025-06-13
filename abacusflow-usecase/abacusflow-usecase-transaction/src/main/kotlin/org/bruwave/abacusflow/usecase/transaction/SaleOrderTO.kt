package org.bruwave.abacusflow.usecase.transaction

import java.time.Instant

data class SaleOrderTO(
    val id: Long,
    val customerId: Long,
    val status: String,
    val items: List<SaleItemTO>,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class SaleItemTO(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double
)

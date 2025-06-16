package org.bruwave.abacusflow.usecase.transaction

import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class SaleOrderTO(
    val id: Long,
    val orderNo: UUID,
    val customerId: Long,
    val status: String,
    val note: String?,
    val items: List<SaleOrderItemTO>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val orderDate: LocalDate
)

data class SaleOrderItemTO(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)

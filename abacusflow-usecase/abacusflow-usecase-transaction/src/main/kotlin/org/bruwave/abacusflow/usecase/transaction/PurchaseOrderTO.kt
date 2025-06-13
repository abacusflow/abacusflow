package org.bruwave.abacusflow.usecase.transaction

import java.time.Instant
import java.util.UUID

data class PurchaseOrderTO(
    val id: Long,
    val orderNo: UUID,
    val supplierId: Long,
    val status: String,
    val items: List<PurchaseOrderItemTO>,
    val createdAt: Instant,
    val updatedAt: Instant
)

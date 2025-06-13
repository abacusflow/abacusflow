package org.bruwave.abacusflow.usecase.transaction

import java.time.Instant

data class BasicPurchaseOrderTO(
    val id: Long,
    val supplierId: Long,
    val status: String,
    val itemCount: Int,
    val createdAt: Instant
)

package org.bruwave.abacusflow.usecase.transaction

import java.time.Instant

data class BasicSaleOrderTO(
    val id: Long,
    val customerId: Long,
    val status: String,
    val itemCount: Int,
    val createdAt: Instant,
)

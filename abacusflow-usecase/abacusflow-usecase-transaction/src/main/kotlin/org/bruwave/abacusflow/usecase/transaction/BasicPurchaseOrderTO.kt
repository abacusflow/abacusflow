package org.bruwave.abacusflow.usecase.transaction

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

data class BasicPurchaseOrderTO(
    val id: Long,
    val orderNo: UUID,
    val supplierName: String,
    val status: String,
    val totalAmount: BigDecimal,
    val totalQuantity: Long,
    val itemCount: Int,
    val orderDate: LocalDate,
    val createdAt: Instant,
    val autoCompleteDate: LocalDate?,
)

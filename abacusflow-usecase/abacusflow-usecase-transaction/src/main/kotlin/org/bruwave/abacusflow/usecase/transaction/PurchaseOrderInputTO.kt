package org.bruwave.abacusflow.usecase.transaction

import java.math.BigDecimal
import java.time.LocalDate

data class CreatePurchaseOrderInputTO(
    val supplierId: Long,
    val orderDate: LocalDate,
    val orderItems: List<PurchaseItemInputTO>,
    val note: String?,
)

data class PurchaseItemInputTO(
    val productId: Long,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val serialNumber: String?,
)

package org.bruwave.abacusflow.usecase.transaction

import java.time.LocalDate

data class CreatePurchaseOrderInputTO(
    val supplierId: Long,
    val orderDate: LocalDate,
    val orderItems: List<PurchaseItemInputTO>,
    val note: String?
)

data class UpdatePurchaseOrderInputTO(
    val supplierId: Long?,
    val orderDate: LocalDate?,
    val orderItems: List<PurchaseItemInputTO>?,
    val note: String?
)
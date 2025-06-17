package org.bruwave.abacusflow.usecase.transaction

data class PurchaseItemInputTO(
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
)

package org.bruwave.abacusflow.usecase.transaction

data class PurchaseItemTO(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double
)
package org.bruwave.abacusflow.usecase.transaction

data class PurchaseOrderItemTO(
    val id: Long,
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double,
    val subtotal: Double
)
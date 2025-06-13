package org.bruwave.abacusflow.usecase.transaction

data class SaleItemInputTO(
    val productId: Long,
    val quantity: Int,
    val unitPrice: Double
)
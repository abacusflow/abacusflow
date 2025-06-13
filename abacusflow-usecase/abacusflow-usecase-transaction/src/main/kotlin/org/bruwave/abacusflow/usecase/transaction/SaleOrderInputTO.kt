package org.bruwave.abacusflow.usecase.transaction

data class CreateSaleOrderInputTO(
    val customerId: Long,
    val items: List<SaleItemInputTO>,
)

data class UpdateSaleOrderInputTO(
    val customerId: Long,
    val items: List<SaleItemInputTO>,
)


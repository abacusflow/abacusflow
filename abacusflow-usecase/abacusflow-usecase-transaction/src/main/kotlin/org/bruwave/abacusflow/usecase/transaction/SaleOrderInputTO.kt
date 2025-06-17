package org.bruwave.abacusflow.usecase.transaction

import java.time.LocalDate

data class CreateSaleOrderInputTO(
    val customerId: Long,
    val orderDate: LocalDate,
    val orderItems: List<SaleItemInputTO>,
    val note: String?,
)

data class UpdateSaleOrderInputTO(
    val customerId: Long?,
    val orderDate: LocalDate?,
    val orderItems: List<SaleItemInputTO>?,
    val note: String?,
)

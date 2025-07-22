package org.abacusflow.usecase.transaction

import java.math.BigDecimal
import java.time.LocalDate

data class CreateSaleOrderInputTO(
    val customerId: Long,
    val orderDate: LocalDate,
    val orderItems: List<SaleItemInputTO>,
    val note: String?,
)

data class SaleItemInputTO(
    val inventoryUnitId: Long,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val discountFactor: BigDecimal?,
)

package org.bruwave.abacusflow.usecase.product

import java.math.BigDecimal
import java.util.UUID

data class BasicProductInstanceTO(
    val id: Long,
    val name: String,
    val serialNumber: String,
    val unitPrice: BigDecimal,
    val productId: Long,
    val productName: String,
    val purchaseOrderNo: UUID,
    val saleOrderNo: UUID?,
)

data class ProductInstanceForBasicProductTO(
    val id: Long,
    val name: String,
    val serialNumber: String,
    val unitPrice: BigDecimal,
    val productId: Long,
)

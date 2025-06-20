package org.bruwave.abacusflow.usecase.product

import java.util.UUID

data class BasicProductInstanceTO(
    val id: Long,
    val name: String,
    val serialNumber: String,
    val productName: String,
    val purchaseOrderNo: UUID,
    val saleOrderNo: UUID?,
)

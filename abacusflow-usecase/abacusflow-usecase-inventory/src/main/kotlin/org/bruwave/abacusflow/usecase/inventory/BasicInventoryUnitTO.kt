package org.bruwave.abacusflow.usecase.inventory

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class BasicInventoryUnitTO(
    val id: Long,
    val title: Long,
    val unitType: String, // "INSTANCE" 或 "BATCH"
    val purchaseOrderNo: UUID,
    val saleOrderNos: List<UUID>,
    val depotId: Long?,
    val quantity: Long,
    val remainingQuantity: Long,
    val unitPrice: Double,
    val receivedAt: Instant,
    val batchCode: UUID?,        // 如果是批次单元
    val serialNumber: String?,   // 如果是资产单元
)


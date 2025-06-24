package org.bruwave.abacusflow.usecase.inventory

import java.time.Instant
import java.util.UUID

data class InventoryUnitTO(
    val id: Long,
    val unitType: String, // INSTANCE 或 BATCH
    val inventoryId: Long,
    val purchaseOrderId: Long,
    val quantity: Long,
    val remainingQuantity: Long,
    val unitPrice: Double,
    val depotId: Long?,
    val status: String, // NORMAL / CONSUMED / CANCELED / REVERSED
    val saleOrderIds: List<Long>,
    val receivedAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
    // 特有字段，视类型动态赋值
    val serialNumber: String?,
    val batchCode: UUID?,
)

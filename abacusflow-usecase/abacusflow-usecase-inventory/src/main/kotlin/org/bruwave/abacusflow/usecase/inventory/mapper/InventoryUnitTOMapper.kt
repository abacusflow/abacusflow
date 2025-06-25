package org.bruwave.abacusflow.usecase.inventory.mapper

import org.bruwave.abacusflow.inventory.InventoryUnit
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitTO

fun InventoryUnit.toTO(): InventoryUnitTO {
    return when (this) {

        is InventoryUnit.InstanceInventoryUnit ->
            InventoryUnitTO(
                id = id,
                type = InventoryUnit.UnitType.INSTANCE.name,
                inventoryId = inventory.id,
                purchaseOrderId = purchaseOrderId,
                quantity = quantity,
                remainingQuantity = remainingQuantity,
                unitPrice = unitPrice,
                depotId = depotId,
                status = status.name,
                saleOrderIds = saleOrderIds,
                receivedAt = receivedAt,
                createdAt = createdAt,
                updatedAt = updatedAt,
                serialNumber = serialNumber,
                batchCode = null,
            )

        is InventoryUnit.BatchInventoryUnit ->
            InventoryUnitTO(
                id = id,
                type = InventoryUnit.UnitType.BATCH.name,
                inventoryId = inventory.id,
                purchaseOrderId = purchaseOrderId,
                quantity = quantity,
                remainingQuantity = remainingQuantity,
                unitPrice = unitPrice,
                depotId = depotId,
                status = status.name,
                saleOrderIds = saleOrderIds,
                receivedAt = receivedAt,
                createdAt = createdAt,
                updatedAt = updatedAt,
                serialNumber = null,
                batchCode = batchCode,
            )

        else -> {
            throw IllegalArgumentException("Unexpected inventory unit type")
        }
    }
}

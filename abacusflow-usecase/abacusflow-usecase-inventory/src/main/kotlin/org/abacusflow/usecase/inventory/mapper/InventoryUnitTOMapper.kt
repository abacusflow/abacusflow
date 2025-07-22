package org.abacusflow.usecase.inventory.mapper

import org.abacusflow.inventory.InventoryUnit
import org.abacusflow.usecase.inventory.InventoryUnitTO

fun InventoryUnit.toTO(): InventoryUnitTO {
    return when (this) {
        is InventoryUnit.InstanceInventoryUnit ->
            InventoryUnitTO(
                id = id,
                type = InventoryUnit.UnitType.INSTANCE.name,
                inventoryId = inventory.id,
                purchaseOrderId = purchaseOrderId,
                initialQuantity = initialQuantity,
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
                initialQuantity = initialQuantity,
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

package org.bruwave.abacusflow.usecase.inventory

import org.bruwave.abacusflow.inventory.InventoryUnit

fun InventoryUnit.toTO(
): InventoryUnitTO {
    return when (this) {
        is InventoryUnit.InstanceInventoryUnit -> InventoryUnitTO(
            id = id,
            unitType = InventoryUnit.UnitType.INSTANCE.toString(),
            depotId = depotId,
            quantity = quantity,
            remainingQuantity = remainingQuantity,
            receivedAt = receivedAt,
            batchCode = null,
            unitPrice = unitPrice,
            serialNumber = serialNumber,
            inventoryId = inventory.id,
            purchaseOrderId = purchaseOrderId,
            status = status.name,
            saleOrderIds = saleOrderIds,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        is InventoryUnit.BatchInventoryUnit -> InventoryUnitTO(
            id = id,
            unitType = InventoryUnit.UnitType.BATCH.toString(),
            depotId = depotId,
            quantity = quantity,
            remainingQuantity = remainingQuantity,
            receivedAt = receivedAt,
            batchCode = batchCode,
            unitPrice = unitPrice,
            serialNumber = null,
            inventoryId = inventory.id,
            purchaseOrderId = purchaseOrderId,
            status = status.name,
            saleOrderIds = saleOrderIds,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        else -> {
            throw IllegalArgumentException("Unexpected inventory unit type")
        }
    }
}
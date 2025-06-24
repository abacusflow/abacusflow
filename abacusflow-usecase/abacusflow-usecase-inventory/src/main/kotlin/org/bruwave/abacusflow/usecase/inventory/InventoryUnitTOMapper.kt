package org.bruwave.abacusflow.usecase.inventory

import org.bruwave.abacusflow.inventory.InventoryUnit
import java.util.UUID

fun InventoryUnit.toBasicTO(
    productName: String,
    purchaseOrderNo: UUID,
    saleOrderNos: List<UUID>,
): BasicInventoryUnitTO {
    return when (this) {
        is InventoryUnit.InstanceInventoryUnit -> BasicInventoryUnitTO(
            id = id,
            title = "${productName}-${serialNumber}",
            unitType = InventoryUnit.UnitType.INSTANCE.toString(),
            purchaseOrderNo = purchaseOrderNo,
            depotId = depotId,
            quantity = quantity,
            remainingQuantity = remainingQuantity,
            saleOrderNos = saleOrderNos,
            receivedAt = receivedAt,
            batchCode = null,
            unitPrice = unitPrice,
            serialNumber = serialNumber,
        )

        is InventoryUnit.BatchInventoryUnit -> BasicInventoryUnitTO(
            id = id,
            title = "${productName}-${batchCode}",
            unitType = InventoryUnit.UnitType.BATCH.toString(),
            purchaseOrderNo = purchaseOrderNo,
            saleOrderNos = saleOrderNos,
            depotId = depotId,
            quantity = quantity,
            remainingQuantity = remainingQuantity,
            receivedAt = receivedAt,
            batchCode = batchCode,
            unitPrice = unitPrice,
            serialNumber = null,
        )

        else -> {
            throw IllegalArgumentException("Unexpected inventory unit type")
        }
    }
}
package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.model.BasicInventoryUnitVO
import org.bruwave.abacusflow.portal.web.model.InventoryUnitStatusVO
import org.bruwave.abacusflow.portal.web.model.InventoryUnitTypeVO
import org.bruwave.abacusflow.portal.web.model.InventoryUnitVO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitTO

fun BasicInventoryUnitTO.toBasicVO(): BasicInventoryUnitVO =
    BasicInventoryUnitVO(
        id = id,
        title = title,
        type = mapInventoryUnitTypeTOToVO(type),
        status = mapInventoryUnitStatusTOToVO(status),
        purchaseOrderNo = purchaseOrderNo,
        saleOrderNos = saleOrderNos,
        depotName = depotName,
        quantity = quantity,
        remainingQuantity = remainingQuantity,
        unitPrice = unitPrice.toDouble(),
        receivedAt = receivedAt.toEpochMilli(),
        batchCode = batchCode,
        serialNumber = serialNumber,
    )

fun InventoryUnitTO.toVO(): InventoryUnitVO =
    InventoryUnitVO(
        id = id,
        unitType = mapInventoryUnitTypeTOToVO(type),
        inventoryId = inventoryId,
        purchaseOrderId = purchaseOrderId,
        quantity = quantity,
        remainingQuantity = remainingQuantity,
        unitPrice = unitPrice.toDouble(),
        depotId = depotId,
        status = mapInventoryUnitStatusTOToVO(status),
        saleOrderIds = saleOrderIds,
        receivedAt = receivedAt.toEpochMilli(),
        serialNumber = serialNumber,
        batchCode = batchCode,
    )

fun mapInventoryUnitTypeTOToVO(type: String): InventoryUnitTypeVO {
    return when (type.uppercase()) {
        "INSTANCE" -> InventoryUnitTypeVO.instance
        "BATCH" -> InventoryUnitTypeVO.batch
        else -> throw IllegalArgumentException("Unsupported inventory unit type $type")
    }
}

fun mapInventoryUnitStatusTOToVO(input: String): InventoryUnitStatusVO {
    return when (input.uppercase()) {
        "NORMAL" -> InventoryUnitStatusVO.normal
        "CONSUMED" -> InventoryUnitStatusVO.consumed
        "CANCELED" -> InventoryUnitStatusVO.canceled
        "REVERSED" -> InventoryUnitStatusVO.reversed
        else -> throw IllegalArgumentException("Unsupported inventory unit type $input")
    }
}

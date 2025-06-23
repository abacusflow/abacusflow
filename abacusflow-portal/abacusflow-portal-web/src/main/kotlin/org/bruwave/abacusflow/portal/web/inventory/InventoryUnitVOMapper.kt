package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.model.BasicInventoryUnitVO
import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.InventoryUnitTypeVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO

fun BasicInventoryUnitTO.toBasicVO(): BasicInventoryUnitVO =
    BasicInventoryUnitVO(
        id = id,
        type = mapInventoryUnitTypeTOToVO(unitType),
        purchaseOrderNo = purchaseOrderNo,
        saleOrderNos = saleOrderNos,
        depotId = depotId,
        quantity = quantity,
        remainingQuantity = remainingQuantity,
        unitPrice = unitPrice,
        receivedAt = receivedAt.toEpochMilli(),
        batchCode = batchCode,
        serialNumber = serialNumber,
    )

fun mapInventoryUnitTypeTOToVO(type: String): InventoryUnitTypeVO {
    return when (type.uppercase()) {
        "INSTANCE" -> InventoryUnitTypeVO.instance
        "BATCH" -> InventoryUnitTypeVO.batch
        else -> throw IllegalArgumentException("Unsupported inventory unit type $type")
    }
}
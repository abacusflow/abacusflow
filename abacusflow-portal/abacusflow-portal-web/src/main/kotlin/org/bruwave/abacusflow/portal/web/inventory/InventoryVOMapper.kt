package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO

fun InventoryTO.toVO(): InventoryVO = InventoryVO(
    id = id,
    productId = productId,
    productName = "null" ?: "未知产品", //TODO-null
    warehouseId = warehouseId,
    warehouseName = "null" ?: "未知仓库",//TODO-null
    quantity = quantity,
    reservedQuantity = reservedQuantity,
    availableQuantity = availableQuantity,
    safetyStock = safetyStock,
    maxStock = maxStock,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli()
)

fun BasicInventoryTO.toBasicVO(): BasicInventoryVO = BasicInventoryVO(
    id = id,
    productName = "null" ?: "未知产品", //TODO-null
    warehouseName = "null" ?: "未知仓库",//TODO-null
    quantity = quantity,
    availableQuantity = availableQuantity,
    safetyStock = safetyStock,
    maxStock = maxStock,
)


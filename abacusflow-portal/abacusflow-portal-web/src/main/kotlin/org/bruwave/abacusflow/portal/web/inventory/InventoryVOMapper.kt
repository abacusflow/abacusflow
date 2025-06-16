package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO

fun InventoryTO.toVO(): InventoryVO = InventoryVO(
    id = id,
    productId = productId,
    warehouseId = warehouseId,
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
    productName = productName,
    warehouseName = warehouseName,
    quantity = quantity,
    availableQuantity = availableQuantity,
    safetyStock = safetyStock,
    maxStock = maxStock,
)


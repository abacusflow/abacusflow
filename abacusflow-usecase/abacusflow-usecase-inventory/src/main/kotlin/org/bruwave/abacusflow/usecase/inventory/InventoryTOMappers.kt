package org.bruwave.abacusflow.usecase.inventory

import org.bruwave.abacusflow.inventory.Inventory


fun Inventory.toBasicTO(productName: String, warehouseName: String) = BasicInventoryTO(
    id = id,
    productName = productName,
    warehouseName = warehouseName,
    quantity = quantity,
    availableQuantity = availableQuantity(),
    safetyStock = safetyStock,
    maxStock = maxStock
)

fun Inventory.toTO() = InventoryTO(
    id = id,
    productId = productId,
    warehouseId = warehouseId,
    quantity = quantity,
    reservedQuantity = reservedQuantity,
    availableQuantity = availableQuantity(),
    safetyStock = safetyStock,
    maxStock = maxStock,
    version = version,
    createdAt = createdAt,
    updatedAt = updatedAt
)

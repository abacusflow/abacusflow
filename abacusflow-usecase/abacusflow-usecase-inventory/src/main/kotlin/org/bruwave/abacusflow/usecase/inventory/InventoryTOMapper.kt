package org.bruwave.abacusflow.usecase.inventory

import org.bruwave.abacusflow.inventory.Inventory

fun Inventory.toBasicTO(
    productName: String,
    depotName: String,
    expectedQuantity: Long,
) = BasicInventoryTO(
    id = id,
    productName = productName,
    depotName = depotName,
    quantity = quantity,
    availableQuantity = availableQuantity,
    safetyStock = safetyStock,
    maxStock = maxStock,
    expectedQuantity = expectedQuantity
)

fun Inventory.toTO() =
    InventoryTO(
        id = id,
        productId = productId,
        depotId = depotId,
        quantity = quantity,
        reservedQuantity = reservedQuantity,
        availableQuantity = availableQuantity,
        safetyStock = safetyStock,
        maxStock = maxStock,
        version = version,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

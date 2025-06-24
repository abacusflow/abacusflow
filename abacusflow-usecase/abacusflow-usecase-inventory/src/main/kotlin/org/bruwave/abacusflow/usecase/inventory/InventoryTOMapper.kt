package org.bruwave.abacusflow.usecase.inventory

import org.bruwave.abacusflow.inventory.Inventory
//
// fun Inventory.toBasicTO(
//    productName: String,
//    units: List<BasicInventoryUnitTO>,
// ) = BasicInventoryTO(
//    id = id,
//    productName = productName,
//    quantity = units.sumOf { it.quantity },
//    safetyStock = safetyStock,
//    maxStock = maxStock,
//    units = units,
// )

fun Inventory.toTO() =
    InventoryTO(
        id = id,
        productId = productId,
        safetyStock = safetyStock,
        maxStock = maxStock,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

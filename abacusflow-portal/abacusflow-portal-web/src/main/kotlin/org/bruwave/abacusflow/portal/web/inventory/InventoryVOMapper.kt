package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO

fun InventoryTO.toVO(): InventoryVO =
    InventoryVO(
        id = id,
        productId = productId,
        depotId = depotId,
        quantity = quantity,
        reservedQuantity = reservedQuantity,
        availableQuantity = availableQuantity,
        safetyStock = safetyStock,
        maxStock = maxStock,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
    )

fun BasicInventoryTO.toBasicVO(): BasicInventoryVO =
    BasicInventoryVO(
        id = id,
        productName = productName,
        depotName = depotName,
        quantity = quantity,
        availableQuantity = availableQuantity,
        expectedQuantity = expectedQuantity,
        safetyStock = safetyStock,
        maxStock = maxStock,
    )

package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO

fun InventoryTO.toVO(): InventoryVO =
    InventoryVO(
        id = id,
        productId = productId,
        quantity = quantity,
        safetyStock = safetyStock,
        maxStock = maxStock,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
    )

fun BasicInventoryTO.toBasicVO(): BasicInventoryVO =
    BasicInventoryVO(
        id = id,
        productName = productName,
        quantity = quantity,
        safetyStock = safetyStock,
        maxStock = maxStock,
    )

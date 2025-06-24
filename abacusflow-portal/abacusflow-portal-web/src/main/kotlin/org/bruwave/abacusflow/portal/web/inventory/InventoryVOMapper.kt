package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.portal.web.model.ProductTypeVO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO

fun InventoryTO.toVO(): InventoryVO =
    InventoryVO(
        id = id,
        productId = productId,
        safetyStock = safetyStock,
        maxStock = maxStock,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
    )

fun BasicInventoryTO.toBasicVO(): BasicInventoryVO =
    BasicInventoryVO(
        id = id,
        productName = productName,
        productType = mapProductTypeTOtoVO(productType),
        quantity = quantity,
        depotNames = depotNames,
        safetyStock = safetyStock,
        maxStock = maxStock,
        units = units.map { it.toBasicVO() },
    )

fun mapProductTypeTOtoVO(input: String): ProductTypeVO {
    return when (input.uppercase()) {
        "MATERIAL" -> ProductTypeVO.material
        "ASSET" -> ProductTypeVO.asset
        else -> throw IllegalArgumentException("Unknown product type $input")
    }
}

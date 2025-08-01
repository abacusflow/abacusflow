package org.abacusflow.portal.web.inventory

import org.abacusflow.portal.web.model.BasicInventoryVO
import org.abacusflow.portal.web.model.InventoryVO
import org.abacusflow.portal.web.model.ProductTypeVO
import org.abacusflow.usecase.inventory.BasicInventoryTO
import org.abacusflow.usecase.inventory.InventoryTO

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
        productSpecification = productSpecification,
        productNote = productNote,
        productType = mapProductTypeTOtoVO(productType),
        initialQuantity = initialQuantity,
        quantity = quantity,
        remainingQuantity = remainingQuantity,
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

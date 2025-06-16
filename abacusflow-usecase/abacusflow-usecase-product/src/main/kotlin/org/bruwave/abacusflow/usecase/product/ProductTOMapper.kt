package org.bruwave.abacusflow.usecase.product

import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.product.ProductUnit

fun Product.toTO() = ProductTO(
    id = id,
    name = name,
    unit = unit.name,
    unitPrice = unitPrice,
    categoryId = category.id,
    categoryName = category.name,
    supplierId = supplierId,
    specification = specification,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Product.toBasicTO() = BasicProductTO(
    id = id,
    name = name,
    categoryName = category.name,
    supplierName = "null", //TODO-NULL
    unit = unit.name,
    unitPrice = unitPrice,
    specification = specification
)

fun mapProductUnitTOToDO(unit: String): ProductUnit = when (unit.uppercase()) {
    "ITEM" -> ProductUnit.ITEM
    "PIECE" -> ProductUnit.PIECE
    "BOX" -> ProductUnit.BOX
    "PACK" -> ProductUnit.PACK
    "DOZEN" -> ProductUnit.DOZEN
    "PAIR" -> ProductUnit.PAIR
    "GRAM" -> ProductUnit.GRAM
    "KILOGRAM" -> ProductUnit.KILOGRAM
    "LITER" -> ProductUnit.LITER
    "MILLILITER" -> ProductUnit.MILLILITER
    "METER" -> ProductUnit.METER
    "CENTIMETER" -> ProductUnit.CENTIMETER
    "BOTTLE" -> ProductUnit.BOTTLE
    "BARREL" -> ProductUnit.BARREL
    "BAG" -> ProductUnit.BAG
    "SHEET" -> ProductUnit.SHEET
    "ROLL" -> ProductUnit.ROLL
    else -> throw IllegalArgumentException("Unknown product unit: $unit")
}

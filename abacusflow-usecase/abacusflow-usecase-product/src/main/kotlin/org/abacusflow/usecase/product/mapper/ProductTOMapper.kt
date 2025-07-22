package org.abacusflow.usecase.product.mapper

import org.abacusflow.product.Product
import org.abacusflow.product.ProductUnit
import org.abacusflow.usecase.product.BasicProductTO
import org.abacusflow.usecase.product.ProductInstanceForBasicProductTO
import org.abacusflow.usecase.product.ProductTO

fun Product.toTO() =
    ProductTO(
        id = id,
        name = name,
        barcode = barcode,
        specification = specification,
        type = type.name,
        unit = unit.name,
        categoryId = category.id,
        note = note,
        enabled = enabled,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun Product.toBasicTO(instances: List<ProductInstanceForBasicProductTO>?) =
    BasicProductTO(
        id = id,
        name = name,
        barcode = barcode,
        specification = specification,
        type = type.name,
        categoryName = category.name,
        unit = unit.name,
        note = note,
        enabled = enabled,
    )

fun mapProductUnitTOToDO(unit: String): ProductUnit =
    when (unit.uppercase()) {
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

fun mapProductTypeTOToDO(type: String): Product.ProductType =
    when (type.uppercase()) {
        "MATERIAL" -> Product.ProductType.MATERIAL
        "ASSET" -> Product.ProductType.ASSET
        else -> throw IllegalArgumentException("Unknown product type: $type")
    }

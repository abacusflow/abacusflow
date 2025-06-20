package org.bruwave.abacusflow.usecase.product.mapper

import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.product.ProductUnit
import org.bruwave.abacusflow.usecase.product.BasicProductInstanceTO
import org.bruwave.abacusflow.usecase.product.BasicProductTO
import org.bruwave.abacusflow.usecase.product.ProductTO

fun Product.toTO() =
    ProductTO(
        id = id,
        name = name,
        specification = specification,
        type = type.name,
        unit = unit.name,
        unitPrice = unitPrice,
        categoryId = category.id,
        supplierId = supplierId,
        note = note,
        enabled = enabled,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun Product.toBasicTO(supplierName: String, instances: List<BasicProductInstanceTO>?) =
    BasicProductTO(
        id = id,
        name = name,
        specification = specification,
        type = type.name,
        categoryName = category.name,
        supplierName = supplierName,
        unit = unit.name,
        unitPrice = unitPrice,
        note = note,
        enabled = enabled,
        instances = instances
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

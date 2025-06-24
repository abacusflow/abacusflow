package org.bruwave.abacusflow.portal.web.product.mapper

import org.bruwave.abacusflow.portal.web.model.BasicProductVO
import org.bruwave.abacusflow.portal.web.model.ProductTypeVO
import org.bruwave.abacusflow.portal.web.model.ProductUnitVO
import org.bruwave.abacusflow.portal.web.model.ProductVO
import org.bruwave.abacusflow.usecase.product.BasicProductTO
import org.bruwave.abacusflow.usecase.product.ProductTO

fun ProductTO.toVO(): ProductVO =
    ProductVO(
        id = id,
        name = name,
        specification = specification,
        type = mapProductTypeTOToVO(type),
        unit = mapProductUnitTOToVO(unit),
        note = note,
        categoryId = categoryId,
        enabled = enabled,
        updatedAt = updatedAt.toEpochMilli(),
        createdAt = createdAt.toEpochMilli(),
    )

fun BasicProductTO.toBasicVO(): BasicProductVO =
    BasicProductVO(
        id = id,
        name = name,
        specification = specification,
        type = mapProductTypeTOToVO(type),
        unit = mapProductUnitTOToVO(unit),
        categoryName = categoryName,
        enabled = enabled,
        note = note,
    )

fun mapProductUnitTOToVO(unit: String): ProductUnitVO =
    when (unit.uppercase()) {
        "ITEM" -> ProductUnitVO.item
        "PIECE" -> ProductUnitVO.piece
        "BOX" -> ProductUnitVO.box
        "PACK" -> ProductUnitVO.pack
        "DOZEN" -> ProductUnitVO.dozen
        "PAIR" -> ProductUnitVO.pair
        "GRAM" -> ProductUnitVO.gram
        "KILOGRAM" -> ProductUnitVO.kilogram
        "LITER" -> ProductUnitVO.liter
        "MILLILITER" -> ProductUnitVO.milliliter
        "METER" -> ProductUnitVO.meter
        "CENTIMETER" -> ProductUnitVO.centimeter
        "BOTTLE" -> ProductUnitVO.bottle
        "BARREL" -> ProductUnitVO.barrel
        "BAG" -> ProductUnitVO.bag
        "SHEET" -> ProductUnitVO.sheet
        "ROLL" -> ProductUnitVO.roll
        else -> throw IllegalArgumentException("Unknown product unit: $unit")
    }

fun mapProductTypeTOToVO(type: String): ProductTypeVO =
    when (type.uppercase()) {
        "MATERIAL" -> ProductTypeVO.material
        "ASSET" -> ProductTypeVO.asset
        else -> throw IllegalArgumentException("Unknown product unit: $type")
    }

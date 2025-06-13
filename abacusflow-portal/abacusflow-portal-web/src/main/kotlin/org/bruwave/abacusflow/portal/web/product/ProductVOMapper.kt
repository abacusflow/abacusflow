package org.bruwave.abacusflow.portal.web.product

import org.bruwave.abacusflow.portal.web.model.BasicProductVO
import org.bruwave.abacusflow.portal.web.model.ProductUnitVO
import org.bruwave.abacusflow.portal.web.model.ProductVO
import org.bruwave.abacusflow.usecase.product.BasicProductTO
import org.bruwave.abacusflow.usecase.product.ProductTO


fun ProductTO.toVO(): ProductVO = ProductVO(
    id = id,
    name = name,
    categoryName = categoryName,
    supplierId = supplierId,
    unit = ProductUnitVO.valueOf(unit),
    unitPrice = unitPrice,
    specification = specification,
    createdAt = createdAt.toEpochMilli(),
    updatedAt = updatedAt.toEpochMilli(),
    categoryId = categoryId,
    supplierName = "null"// TODO-NULL
)

fun BasicProductTO.toVO(): BasicProductVO = BasicProductVO(
    id = id,
    name = name,
    unit = ProductUnitVO.valueOf(unit),
    unitPrice = unitPrice,
    categoryName = categoryName,
    supplierName = supplierName // TODO()
)

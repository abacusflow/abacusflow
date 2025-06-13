package org.bruwave.abacusflow.usecase.product

import org.bruwave.abacusflow.product.Product

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
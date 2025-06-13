package org.bruwave.abacusflow.usecase.product

import org.bruwave.abacusflow.product.ProductCategory

fun ProductCategory.toTO() = ProductCategoryTO(
    id = id,
    name = name,
    description = description,
    parentId = parent.id,
    parentName = parent.name,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun ProductCategory.toBasicTO() = BasicProductCategoryTO(
    id = id,
    name = name,
    parentName = parent.name,
    createdAt = createdAt
)
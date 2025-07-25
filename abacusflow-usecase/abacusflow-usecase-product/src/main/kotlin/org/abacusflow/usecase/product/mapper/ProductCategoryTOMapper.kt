package org.abacusflow.usecase.product.mapper

import org.abacusflow.product.ProductCategory
import org.abacusflow.usecase.product.BasicProductCategoryTO
import org.abacusflow.usecase.product.ProductCategoryTO

fun ProductCategory.toTO() =
    ProductCategoryTO(
        id = id,
        name = name,
        description = description,
        parentId = parent?.id,
        parentName = parent?.name,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun ProductCategory.toBasicTO() =
    BasicProductCategoryTO(
        id = id,
        name = name,
        parentName = parent?.name,
        createdAt = createdAt,
    )

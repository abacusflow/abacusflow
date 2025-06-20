package org.bruwave.abacusflow.usecase.product.mapper

import org.bruwave.abacusflow.product.ProductCategory
import org.bruwave.abacusflow.usecase.product.BasicProductCategoryTO
import org.bruwave.abacusflow.usecase.product.ProductCategoryTO

fun ProductCategory.toTO() =
    ProductCategoryTO(
        id = id,
        name = name,
        description = description,
        parentId = parent?.id,
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

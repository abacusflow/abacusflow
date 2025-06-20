package org.bruwave.abacusflow.portal.web.product.mapper

import org.bruwave.abacusflow.portal.web.model.BasicProductCategoryVO
import org.bruwave.abacusflow.portal.web.model.ProductCategoryVO
import org.bruwave.abacusflow.usecase.product.BasicProductCategoryTO
import org.bruwave.abacusflow.usecase.product.ProductCategoryTO

fun ProductCategoryTO.toVO(): ProductCategoryVO =
    ProductCategoryVO(
        id = id,
        name = name,
        createdAt = createdAt.toEpochMilli(),
        updatedAt = updatedAt.toEpochMilli(),
        description = description,
        parentId = parentId,
    )

fun BasicProductCategoryTO.toVO(): BasicProductCategoryVO =
    BasicProductCategoryVO(
        id = id,
        name = name,
        parentName = parentName,
    )

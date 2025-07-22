package org.abacusflow.portal.web.product.mapper

import org.abacusflow.portal.web.model.BasicProductCategoryVO
import org.abacusflow.portal.web.model.ProductCategoryVO
import org.abacusflow.usecase.product.BasicProductCategoryTO
import org.abacusflow.usecase.product.ProductCategoryTO

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

package org.abacusflow.usecase.product

import java.time.Instant

data class BasicProductCategoryTO(
    val id: Long,
    val name: String,
    val parentName: String?,
    val createdAt: Instant,
)

data class ProductCategoryTO(
    val id: Long,
    val name: String,
    val description: String?,
    val parentId: Long?,
    val parentName: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

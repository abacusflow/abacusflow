package org.bruwave.abacusflow.usecase.product

import java.time.Instant

data class ProductCategoryTO(
    val id: Long,
    val name: String,
    val description: String?,
    val parentId: Long?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

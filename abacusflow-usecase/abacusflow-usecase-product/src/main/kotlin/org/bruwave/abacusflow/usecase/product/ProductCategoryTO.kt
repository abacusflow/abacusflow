package org.bruwave.abacusflow.usecase.product

import java.time.Instant

data class ProductCategoryTO(
    val id: Long,
    val name: String,
    val code: String,
    val description: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)
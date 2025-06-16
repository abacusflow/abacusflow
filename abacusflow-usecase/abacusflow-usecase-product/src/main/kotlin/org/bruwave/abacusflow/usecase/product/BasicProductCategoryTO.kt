package org.bruwave.abacusflow.usecase.product

import java.time.Instant

data class BasicProductCategoryTO(
    val id: Long,
    val name: String,
    val parentName: String?,
    val createdAt: Instant,
)
package org.bruwave.abacusflow.usecase.product

import java.time.Instant

data class ProductTO(
    val id: Long,
    val name: String,
    val unitPrice: Double,
    val categoryId: Long,
    val categoryName: String,
    val supplierId: Long,
    val specification: String?,
    val createdAt: Instant,
    val updatedAt: Instant
) 
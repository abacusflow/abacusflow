package org.bruwave.abacusflow.usecase.product

import java.time.Instant

data class ProductTO(
    val id: Long,
    val name: String,
    val unit: String,
    val unitPrice: Double,
    val categoryId: Long,
    val supplierId: Long,
    val specification: String?,
    val enabled: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant
) 
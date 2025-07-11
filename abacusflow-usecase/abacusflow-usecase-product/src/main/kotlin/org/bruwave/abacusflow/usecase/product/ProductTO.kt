package org.bruwave.abacusflow.usecase.product

import java.time.Instant

data class BasicProductTO(
    val id: Long,
    val name: String,
    val barcode: String,
    val specification: String?,
    val type: String,
    val categoryName: String,
    val unit: String,
    val note: String?,
    val enabled: Boolean,
)

data class ProductTO(
    val id: Long,
    val type: String,
    val name: String,
    val barcode: String,
    val specification: String?,
    val unit: String,
    val categoryId: Long,
    val note: String?,
    val enabled: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
)

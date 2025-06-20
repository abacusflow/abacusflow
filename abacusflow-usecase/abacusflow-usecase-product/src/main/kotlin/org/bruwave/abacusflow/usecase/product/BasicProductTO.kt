package org.bruwave.abacusflow.usecase.product

data class BasicProductTO(
    val id: Long,
    val name: String,
    val specification: String?,
    val type: String,
    val categoryName: String,
    val supplierName: String,
    val unit: String,
    val unitPrice: Double,
    val note: String?,
    val enabled: Boolean,
)

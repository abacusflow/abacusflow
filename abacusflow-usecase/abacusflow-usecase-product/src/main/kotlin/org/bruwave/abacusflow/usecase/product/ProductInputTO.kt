package org.bruwave.abacusflow.usecase.product

data class CreateProductInputTO(
    val name: String,
    val type: String,
    val barcode: String,
    val specification: String?,
    val categoryId: Long,
    val unit: String,
    val note: String?,
)

data class UpdateProductInputTO(
    val name: String?,
    val type: String?,
    val specification: String?,
    val categoryId: Long?,
    val unit: String?,
    val note: String?,
)

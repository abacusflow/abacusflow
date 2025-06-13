package org.bruwave.abacusflow.usecase.product

data class CreateProductInputTO(
    val name: String,
    val categoryId: Long,
    val supplierId: Long,
    val unitPrice: Double,
    val specification: String
)

data class UpdateProductInputTO(
    val name: String,
    val categoryId: Long,
    val supplierId: Long,
    val unitPrice: Double,
    val specification: String
)
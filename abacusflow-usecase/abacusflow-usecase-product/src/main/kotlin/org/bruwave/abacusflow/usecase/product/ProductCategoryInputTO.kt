package org.bruwave.abacusflow.usecase.product

data class CreateProductCategoryInputTO(
    val name: String,
    val code: String,
    val description: String?
)

data class UpdateProductCategoryInputTO(
    val name: String,
    val code: String,
    val description: String?,
)
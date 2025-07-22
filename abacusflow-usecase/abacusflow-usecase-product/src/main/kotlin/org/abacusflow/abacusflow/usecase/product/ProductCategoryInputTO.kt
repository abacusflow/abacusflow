package org.abacusflow.usecase.product

data class CreateProductCategoryInputTO(
    val name: String,
    val parentId: Long,
    val description: String?,
)

data class UpdateProductCategoryInputTO(
    val name: String?,
    val parentId: Long?,
    val description: String?,
)

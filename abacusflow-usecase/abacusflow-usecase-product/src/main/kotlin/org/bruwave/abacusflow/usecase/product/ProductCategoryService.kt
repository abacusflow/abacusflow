package org.bruwave.abacusflow.usecase.product

interface ProductCategoryService {
    fun createProductCategory(input: CreateProductCategoryInputTO): ProductCategoryTO

    fun updateProductCategory(
        id: Long,
        input: UpdateProductCategoryInputTO,
    ): ProductCategoryTO

    fun deleteProductCategory(id: Long): ProductCategoryTO

    fun getProductCategory(id: Long): ProductCategoryTO

    fun listProductCategories(): List<BasicProductCategoryTO>
}

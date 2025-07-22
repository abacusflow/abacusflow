package org.abacusflow.usecase.product.service

import org.abacusflow.usecase.product.CreateProductCategoryInputTO
import org.abacusflow.usecase.product.ProductCategoryTO
import org.abacusflow.usecase.product.UpdateProductCategoryInputTO

interface ProductCategoryCommandService {
    fun createProductCategory(input: CreateProductCategoryInputTO): ProductCategoryTO

    fun updateProductCategory(
        id: Long,
        input: UpdateProductCategoryInputTO,
    ): ProductCategoryTO

    fun deleteProductCategory(id: Long): ProductCategoryTO
}

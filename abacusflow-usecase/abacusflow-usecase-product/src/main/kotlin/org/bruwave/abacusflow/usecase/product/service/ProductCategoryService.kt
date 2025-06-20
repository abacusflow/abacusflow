package org.bruwave.abacusflow.usecase.product.service

import org.bruwave.abacusflow.usecase.product.BasicProductCategoryTO
import org.bruwave.abacusflow.usecase.product.CreateProductCategoryInputTO
import org.bruwave.abacusflow.usecase.product.ProductCategoryTO
import org.bruwave.abacusflow.usecase.product.UpdateProductCategoryInputTO

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
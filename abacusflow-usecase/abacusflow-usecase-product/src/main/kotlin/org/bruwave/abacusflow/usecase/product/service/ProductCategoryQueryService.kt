package org.bruwave.abacusflow.usecase.product.service

import org.bruwave.abacusflow.usecase.product.BasicProductCategoryTO
import org.bruwave.abacusflow.usecase.product.CreateProductCategoryInputTO
import org.bruwave.abacusflow.usecase.product.ProductCategoryTO
import org.bruwave.abacusflow.usecase.product.UpdateProductCategoryInputTO

interface ProductCategoryQueryService {
    fun getProductCategory(id: Long): ProductCategoryTO

    fun listProductCategories(): List<BasicProductCategoryTO>
}

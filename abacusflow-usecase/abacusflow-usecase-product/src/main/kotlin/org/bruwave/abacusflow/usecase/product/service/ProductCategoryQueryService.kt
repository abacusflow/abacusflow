package org.bruwave.abacusflow.usecase.product.service

import org.bruwave.abacusflow.usecase.product.BasicProductCategoryTO
import org.bruwave.abacusflow.usecase.product.ProductCategoryTO

interface ProductCategoryQueryService {
    fun getProductCategory(id: Long): ProductCategoryTO

    fun listBasicProductCategories(): List<BasicProductCategoryTO>

    fun listProductCategories(): List<ProductCategoryTO>
}

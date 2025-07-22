package org.abacusflow.usecase.product.service

import org.abacusflow.usecase.product.BasicProductCategoryTO
import org.abacusflow.usecase.product.ProductCategoryTO

interface ProductCategoryQueryService {
    fun getProductCategory(id: Long): ProductCategoryTO

    fun listBasicProductCategories(): List<BasicProductCategoryTO>

    fun listProductCategories(): List<ProductCategoryTO>
}

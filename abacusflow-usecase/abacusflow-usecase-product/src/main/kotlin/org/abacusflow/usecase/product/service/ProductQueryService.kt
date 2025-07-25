package org.abacusflow.usecase.product.service

import org.abacusflow.usecase.product.BasicProductTO
import org.abacusflow.usecase.product.ProductTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductQueryService {
    fun listBasicProductsPage(
        pageable: Pageable,
        name: String?,
        type: String?,
        enabled: Boolean?,
        categoryId: Long?,
    ): Page<BasicProductTO>

    fun listProducts(): List<ProductTO>

    fun getProduct(id: Long): ProductTO

    fun getProduct(name: String): ProductTO
}

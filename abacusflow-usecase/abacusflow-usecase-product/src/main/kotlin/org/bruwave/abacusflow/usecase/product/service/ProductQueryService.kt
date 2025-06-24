package org.bruwave.abacusflow.usecase.product.service

import org.bruwave.abacusflow.usecase.product.BasicProductTO
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductQueryService {
    fun getProduct(id: Long): ProductTO

    fun getProduct(name: String): ProductTO

    fun listProductsPage(
        pageable: Pageable,
        name: String?,
        type: String?,
        enabled: Boolean?,
        categoryId: Long?,
    ): Page<BasicProductTO>
}

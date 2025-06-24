package org.bruwave.abacusflow.usecase.product.service

import org.bruwave.abacusflow.usecase.product.BasicProductTO
import org.bruwave.abacusflow.usecase.product.CreateProductInputTO
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.bruwave.abacusflow.usecase.product.UpdateProductInputTO

interface ProductQueryService {
    fun getProduct(id: Long): ProductTO

    fun getProduct(name: String): ProductTO

    fun listProducts(categoryId: Long?): List<BasicProductTO>
}

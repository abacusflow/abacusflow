package org.bruwave.abacusflow.usecase.product.service

import org.bruwave.abacusflow.usecase.product.BasicProductTO
import org.bruwave.abacusflow.usecase.product.CreateProductInputTO
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.bruwave.abacusflow.usecase.product.UpdateProductInputTO

interface ProductService {
    fun createProduct(input: CreateProductInputTO): ProductTO

    fun updateProduct(
        id: Long,
        input: UpdateProductInputTO,
    ): ProductTO

    fun deleteProduct(id: Long): ProductTO

    fun getProduct(id: Long): ProductTO

    fun getProduct(name: String): ProductTO

    fun listProducts(categoryId: Long?): List<BasicProductTO>
}

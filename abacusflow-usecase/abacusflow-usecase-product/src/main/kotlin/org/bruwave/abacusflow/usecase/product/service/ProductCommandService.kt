package org.bruwave.abacusflow.usecase.product.service

import org.bruwave.abacusflow.usecase.product.CreateProductInputTO
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.bruwave.abacusflow.usecase.product.UpdateProductInputTO

interface ProductCommandService {
    fun createProduct(input: CreateProductInputTO): ProductTO

    fun updateProduct(
        id: Long,
        input: UpdateProductInputTO,
    ): ProductTO

    fun deleteProduct(id: Long): ProductTO
}

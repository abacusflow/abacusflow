package org.abacusflow.usecase.product.service

import org.abacusflow.usecase.product.CreateProductInputTO
import org.abacusflow.usecase.product.ProductTO
import org.abacusflow.usecase.product.UpdateProductInputTO

interface ProductCommandService {
    fun createProduct(input: CreateProductInputTO): ProductTO

    fun updateProduct(
        id: Long,
        input: UpdateProductInputTO,
    ): ProductTO

    fun deleteProduct(id: Long): ProductTO
}

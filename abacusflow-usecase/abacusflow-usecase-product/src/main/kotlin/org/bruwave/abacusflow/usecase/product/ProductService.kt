package org.bruwave.abacusflow.usecase.product

interface ProductService {
    fun createProduct(input: CreateProductInputTO): ProductTO

    fun updateProduct(
        id: Long,
        input: UpdateProductInputTO,
    ): ProductTO

    fun deleteProduct(id: Long): ProductTO

    fun getProduct(id: Long): ProductTO

    fun getProduct(name: String): ProductTO

    fun listProducts(): List<BasicProductTO>
}

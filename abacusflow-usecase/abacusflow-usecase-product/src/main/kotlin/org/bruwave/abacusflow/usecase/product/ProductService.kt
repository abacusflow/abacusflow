package org.bruwave.abacusflow.usecase.product

interface ProductService {
    fun createProduct(product: ProductTO): ProductTO
    fun updateProduct(productTO: ProductTO): ProductTO
    fun deleteProduct(productTO: ProductTO): ProductTO
    fun getProduct(id: Long): ProductTO
    fun getProduct(name: String): ProductTO
    fun listProducts(): List<ProductTO>
    fun listProductsByCategory(categoryId: Long): List<ProductTO>
    fun listProductsBySupplier(supplierId: Long): List<ProductTO>
} 
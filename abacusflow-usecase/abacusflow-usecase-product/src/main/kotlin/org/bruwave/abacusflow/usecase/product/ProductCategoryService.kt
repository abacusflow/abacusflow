package org.bruwave.abacusflow.usecase.product

interface ProductCategoryService {
    fun createCategory(category: ProductCategoryTO): ProductCategoryTO
    fun updateCategory(categoryTO: ProductCategoryTO): ProductCategoryTO
    fun deleteCategory(categoryTO: ProductCategoryTO): ProductCategoryTO
    fun getCategory(id: Long): ProductCategoryTO
    fun getCategory(name: String): ProductCategoryTO
    fun getCategoryByCode(code: String): ProductCategoryTO
    fun listCategories(): List<ProductCategoryTO>
} 
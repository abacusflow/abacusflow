package org.bruwave.abacusflow.db.repository

import org.bruwave.abacusflow.product.ProductCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductCategoryRepository : JpaRepository<ProductCategory, Long> {
    fun findByName(name: String): ProductCategory?
    fun findByCode(code: String): ProductCategory?
    fun existsByName(name: String): Boolean
    fun existsByCode(code: String): Boolean
} 
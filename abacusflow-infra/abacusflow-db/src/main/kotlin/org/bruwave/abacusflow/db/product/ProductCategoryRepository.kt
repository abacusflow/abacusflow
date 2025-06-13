package org.bruwave.abacusflow.db.product

import org.bruwave.abacusflow.product.ProductCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductCategoryRepository : JpaRepository<ProductCategory, Long> {
    fun findByName(name: String): ProductCategory?
    fun existsByName(name: String): Boolean
}
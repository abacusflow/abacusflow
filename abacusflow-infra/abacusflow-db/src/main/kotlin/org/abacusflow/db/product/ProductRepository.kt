package org.abacusflow.db.product

import org.abacusflow.product.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByName(name: String): Product?

    fun existsByName(name: String): Boolean

    fun findByCategoryId(categoryId: Long): List<Product>

    fun findByCategoryIdIn(categoryIds: List<Long>): List<Product>

    fun countProductByCategoryId(id: Long): Int
}

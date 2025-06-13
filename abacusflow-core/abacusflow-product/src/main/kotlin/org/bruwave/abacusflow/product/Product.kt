package org.bruwave.abacusflow.product

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant

@Entity
@Table(name = "products")
class Product(
    name: String,
    unitPrice: Double = 0.0,
    category: ProductCategory,
    supplierId: Long
) : AbstractAggregateRoot<Product>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @field:NotBlank
    @field:Size(max = 100)
    var name: String = name
        private set

    @field:PositiveOrZero
    var unitPrice: Double = unitPrice
        private set

    @ManyToOne(cascade = [(CascadeType.ALL)])
    var category: ProductCategory = category
        private set

    var supplierId: Long = supplierId // 通过ID关联供应商，不直接引用
        private set

    @field:Size(max = 50)
    var specification: String? = null
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    fun updateProductBasic(
        newName: String,
        newSpecification: String?,
        newPrice: Double,
        newCategory: ProductCategory
    ) {
        name = newName
        specification = newSpecification
        unitPrice = newPrice
        category = newCategory
        updatedAt = Instant.now()
    }

    fun changeSupplier(newSupplierId: Long) {
        supplierId = newSupplierId
    }
}
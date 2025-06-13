package org.bruwave.abacusflow.product

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
    specification: String?,
    unit: ProductUnit,
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

    @field:NotBlank
    @Enumerated(EnumType.STRING)
    var unit: ProductUnit = unit
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
    var specification: String? = specification
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    fun updateBasicInfo(
        newName: String?,
        newSpecification: String?,
        newUnit: ProductUnit?,
        newUnitPrice: Double?,
    ) {
        newName?.let {
            name = newName
        }
        newSpecification?.let {
            specification = newSpecification
        }
        newUnit?.let {
            unit = newUnit
        }

        newUnitPrice?.let {
            unitPrice = newUnitPrice
        }

        updatedAt = Instant.now()
    }

    fun changeCategory(newCategory: ProductCategory) {
        if (category == newCategory) return

        category = newCategory

        updatedAt = Instant.now()
    }

    fun changeSupplier(newSupplierId: Long) {
        if (supplierId == newSupplierId) return

        supplierId = newSupplierId

        updatedAt = Instant.now()
    }
}
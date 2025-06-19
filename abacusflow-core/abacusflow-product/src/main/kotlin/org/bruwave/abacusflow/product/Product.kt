package org.bruwave.abacusflow.product

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.PrePersist
import jakarta.persistence.PreRemove
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
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
    supplierId: Long,
    private val isNew: Boolean = false, // 标志是否是新建的
) : AbstractAggregateRoot<Product>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @field:NotBlank
    @field:Size(max = 100)
    var name: String = name
        private set

    @field:NotNull
    @Enumerated(EnumType.STRING)
    var unit: ProductUnit = unit
        private set

    @field:PositiveOrZero
    var unitPrice: Double = unitPrice
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: ProductCategory = category
        private set

    var supplierId: Long = supplierId // 通过ID关联供应商，不直接引用
        private set

    @field:Size(max = 50)
    var specification: String? = specification
        private set

    var enabled: Boolean = true
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

//    init {
//        registerEvent(ProductCreatedEvent(this))
//    }

    @PrePersist
    fun prePersist() {
        registerEvent(ProductCreatedEvent(this))
    }

//    // update最佳实践是在每个单独方法,或者说不应该使用ProductUpdatedEvent这么宽泛的事件
//    @PreUpdate
//    fun preUpdate() {
//        registerEvent(ProductUpdatedEvent(this))
//    }

    @PreRemove
    fun preRemove() {
        registerEvent(ProductDeletedEvent(this))
    }

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

    fun enable() {
        if (enabled) return

        enabled = true
        updatedAt = Instant.now()
    }

    fun disable() {
        if (!enabled) return

        enabled = false
        updatedAt = Instant.now()
    }
}

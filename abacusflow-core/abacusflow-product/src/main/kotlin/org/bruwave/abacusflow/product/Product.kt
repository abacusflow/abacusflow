package org.bruwave.abacusflow.product

import jakarta.persistence.Column
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
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant

@Entity
@Table(name = "products")
class Product(
    name: String,
    @Enumerated(EnumType.STRING)
    val type: ProductType = ProductType.MATERIAL,
    specification: String?,
    unit: ProductUnit,
    category: ProductCategory,
    note: String?,
) : AbstractAggregateRoot<Product>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @field:NotBlank
    @field:Size(max = 100)
    var name: String = name
        private set

    @field:Size(max = 50)
    var specification: String? = specification
        private set

    @field:NotNull
    @Enumerated(EnumType.STRING)
    var unit: ProductUnit = unit
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    var category: ProductCategory = category
        private set

    @Column(columnDefinition = "TEXT")
    @field:Size(max = 10000)
    var note: String? = note
        private set

    var enabled: Boolean = true
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    fun updateBasicInfo(
        newName: String?,
        newCategory: ProductCategory?,
        newSpecification: String?,
        newNote: String?,
        newUnit: ProductUnit?,
    ) {
        newName?.let {
            name = newName
        }
        newCategory?.let {
            category = newCategory
        }
        newSpecification?.let {
            specification = newSpecification
        }
        newNote?.let {
            note = newNote
        }
        newUnit?.let {
            unit = newUnit
        }

        updatedAt = Instant.now()
    }

    fun changeCategory(newCategory: ProductCategory) {
        if (category == newCategory) return

        category = newCategory

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

    enum class ProductType {
        MATERIAL, // 普通产品，按数量采购
        ASSET, // 一物一码，按实例采购
    }
}

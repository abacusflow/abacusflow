package org.bruwave.abacusflow.product

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.jetbrains.annotations.NotNull
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "products")
class Product(
    @field:NotBlank(message = "商品名称不能为空")
    @field:Size(max = 100, message = "商品名称不能超过100字符")
    val name: String,

    @field:Size(max = 50, message = "规格不能超过50字符")
    val specification: String? = null,

    @field:PositiveOrZero(message = "价格不能为负数")
    val unitPrice: Double = 0.0,

    @field:Size(max = 50, message = "分类不能超过50字符")
    val category: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    val supplier: Supplier? = null
) : AbstractAggregateRoot<Product>() {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @NotNull
    val createdAt: Instant = Instant.EPOCH

    @UpdateTimestamp
    @NotNull
    var updatedAt: Instant = Instant.EPOCH
        private set

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val inventories: MutableSet<Inventory> = mutableSetOf()

    fun updateProduct(
        newName: String,
        newSpecification: String?,
        newPrice: Double,
        newCategory: String?
    ) {
        name = newName
        specification = newSpecification
        unitPrice = newPrice
        category = newCategory
        updatedAt = Instant.now()
    }
}
package org.bruwave.abacusflow.product

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "product_categories")
class ProductCategory(
    name: String,
    description: String?,
    parent: ProductCategory?,
) {
    @field:NotBlank
    @field:Size(max = 100)
    var name: String = name
        private set

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "parent_id", nullable = true)
    var parent: ProductCategory? = parent
        private set

    @field:Size(max = 500)
    var description: String? = description
        private set

    //    @field:NotBlank
//    @field:Size(max = 50)
//    @Column(unique = true)
//    var code: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()

    fun updateBasicInfo(
        newName: String?,
        newDescription: String?,
    ) {
        newName?.let {
            name = newName
        }
        newDescription?.let {
            description = newDescription
        }

        updatedAt = Instant.now()
    }

    fun changeParent(newParent: ProductCategory) {
        if (parent == newParent) return
        require(newParent != this) { "Categories cannot be set as their own parent category" }

        parent = newParent

        updatedAt = Instant.now()
    }
}

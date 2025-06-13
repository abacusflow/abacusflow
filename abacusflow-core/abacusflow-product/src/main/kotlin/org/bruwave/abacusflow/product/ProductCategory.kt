package org.bruwave.abacusflow.product

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.Instant

@Entity
@Table(name = "product_categories")
class ProductCategory(
    @field:NotBlank
    @field:Size(max = 100)
    var name: String,

    @field:NotBlank
    @field:Size(max = 50)
    @Column(unique = true)
    var code: String,

    @field:Size(max = 500)
    var description: String? = null
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
}
package org.bruwave.abacusflow.inventory

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant

@Entity
@Table(
    name = "inventories",
    uniqueConstraints = [UniqueConstraint(columnNames = ["product_id"])],
)
class Inventory(
    @Column(name = "product_id", nullable = false)
    val productId: Long, // 通过ID关联产品
) : AbstractAggregateRoot<Inventory>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @field:PositiveOrZero
    var safetyStock: Long = 1
        // 安全库存量
        private set

    @field:PositiveOrZero
    var maxStock: Long = 10 // 安全库存量
        private set

//    @OneToMany(mappedBy = "inventory", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
//    val unitsMutable: MutableList<InventoryUnit> = mutableListOf()
//
//    val units: List<InventoryUnit>
//        get() = unitsMutable.toList()

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    fun adjustWarningLine(
        newSafetyStock: Long,
        newMaxStock: Long,
    ) {
        require(newSafetyStock >= 0) { "安全库存不能为负数" }
        require(newMaxStock >= newSafetyStock) { "最大库存必须大于或等于安全库存" }

        this.safetyStock = newSafetyStock
        this.maxStock = newMaxStock
        this.updatedAt = Instant.now()
    }
}

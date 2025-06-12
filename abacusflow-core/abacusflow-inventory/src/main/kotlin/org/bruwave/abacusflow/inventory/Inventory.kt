package org.bruwave.abacusflow.inventory

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Version
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jdk.jfr.internal.SecuritySupport.registerEvent
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "inventories")
class Inventory(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    val product: Product,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "warehouse_id")
    val warehouse: Warehouse,

    @field:PositiveOrZero(message = "库存数量不能为负数")
    var quantity: Int = 0,

    @field:PositiveOrZero(message = "安全库存不能为负数")
    val safetyStock: Int = 5
) : AbstractAggregateRoot<Inventory>() {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID()

    @Version
    var version: Long = 0
        private set

    @CreationTimestamp
    @NotNull
    val createdAt: Instant = Instant.EPOCH

    @UpdateTimestamp
    @NotNull
    var updatedAt: Instant = Instant.EPOCH
        private set

    fun increaseQuantity(amount: Int) {
        require(amount > 0) { "增加数量必须为正数" }
        quantity += amount
        updatedAt = Instant.now()
        registerEvent(InventoryIncreasedEvent(this, amount))
    }

    fun decreaseQuantity(amount: Int) {
        require(amount > 0) { "减少数量必须为正数" }
        require(quantity >= amount) { "库存不足" }
        quantity -= amount
        version++  // 乐观锁版本更新
        updatedAt = Instant.now()
        registerEvent(InventoryDecreasedEvent(this, amount))
    }

    fun isBelowSafetyStock(): Boolean = quantity < safetyStock
}
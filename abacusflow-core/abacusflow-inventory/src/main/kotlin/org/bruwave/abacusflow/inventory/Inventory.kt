package org.bruwave.abacusflow.inventory
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant

@Entity
@Table(name = "inventories")
class Inventory(
    val productId: Long,  // 通过ID关联商品

    val warehouseId: Long,  // 通过ID关联仓库

    @field:PositiveOrZero
    var quantity: Int = 0,

    @field:PositiveOrZero
    val safetyStock: Int = 5
) : AbstractAggregateRoot<Inventory>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Version
    var version: Long = 0
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    fun increaseQuantity(amount: Int) {
        require(amount > 0) { "增加数量必须为正数" }
        quantity += amount
        updatedAt = Instant.now()
        registerEvent(InventoryIncreasedEvent(id, productId, warehouseId, amount))
    }

    fun decreaseQuantity(amount: Int) {
        require(amount > 0) { "减少数量必须为正数" }
        require(quantity >= amount) { "库存不足" }
        quantity -= amount
        version++
        updatedAt = Instant.now()
        registerEvent(InventoryDecreasedEvent(id, productId, warehouseId, amount))
    }

    fun isBelowSafetyStock(): Boolean = quantity < safetyStock
}
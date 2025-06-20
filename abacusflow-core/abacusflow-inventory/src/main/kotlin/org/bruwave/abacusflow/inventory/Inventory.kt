package org.bruwave.abacusflow.inventory

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.persistence.Version
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
    val productId: Long, // 通过ID关联商品
    depotId: Long?,
    quantity: Long = 0,
    reservedQuantity: Long = 0,
) : AbstractAggregateRoot<Inventory>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    var depotId: Long? = depotId // 通过ID关联仓库
        private set

    @field:PositiveOrZero
    var quantity: Long = quantity // 当前库存
        private set

    @field:PositiveOrZero
    var reservedQuantity: Long = reservedQuantity // 被锁定、预留或冻结的库存数量
        private set

    @field:PositiveOrZero
    var safetyStock: Long = 1
        // 安全库存量
        private set

    @field:PositiveOrZero
    var maxStock: Long = 10 // 安全库存量
        private set

    @Version
    var version: Long = 0
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    fun resetQuantity(amount: Long) {
        require(amount >= 0) { "库存数量必须为正数" }
        quantity = amount
        updatedAt = Instant.now()
//        registerEvent(InventoryIncreasedEvent(id, productId, depotId, amount))
    }

    fun increaseQuantity(amount: Int) {
        require(amount > 0) { "增加数量必须为正数" }
        require(amount <= MAX_ADJUSTMENT) { "每次减少的库存数量不能超过 $MAX_ADJUSTMENT 个" }
        quantity += amount
        updatedAt = Instant.now()
//        registerEvent(InventoryIncreasedEvent(id, productId, depotId, amount))
    }

    fun decreaseQuantity(amount: Int) {
        require(amount > 0) { "减少数量必须为正数" }
        require(amount <= MAX_ADJUSTMENT) { "每次减少的库存数量不能超过 $MAX_ADJUSTMENT 个" }
        require(quantity >= amount) { "库存不足" }
        quantity -= amount
        version++
        updatedAt = Instant.now()
//        registerEvent(InventoryDecreasedEvent(id, productId, depotId, amount))
    }

    fun reserveInventory(amount: Int) {
        require(amount > 0) { "冻结数量必须为正数" }
        require(amount <= MAX_ADJUSTMENT) { "每次冻结的库存数量不能超过 $MAX_ADJUSTMENT 个" }
        require(availableQuantity >= amount) { "可用库存不足，无法冻结 $amount 个库存" }

        reservedQuantity += amount
        updatedAt = Instant.now()

//        registerEvent(InventoryReservedEvent(id, productId, depotId, amount))
    }

    fun assignDepot(newDepotId: Long) {
        require(newDepotId > 0) { "无效的仓库ID" }

        if (this.depotId == newDepotId) return

        // 实际上需要将字段变为 var 才能赋值
        this.depotId = newDepotId
        this.updatedAt = Instant.now()

//        registerEvent(DepotAssignedEvent(id, productId, newDepotId))
    }

    fun adjustWarningLine(
        newSafetyStock: Long,
        newMaxStock: Long,
    ) {
        require(newSafetyStock >= 0) { "安全库存不能为负数" }
        require(newMaxStock >= newSafetyStock) { "最大库存必须大于或等于安全库存" }

        this.safetyStock = newSafetyStock
        this.maxStock = newMaxStock
        this.updatedAt = Instant.now()

//        registerEvent(WarningLineAdjustedEvent(id, productId, depotId, newSafetyStock, newMaxStock))
    }

    val isBelowSafetyStock
        get(): Boolean = quantity < safetyStock

    val availableQuantity
        get(): Long = quantity - reservedQuantity

    companion object {
        private const val MAX_ADJUSTMENT = 100
    }
}

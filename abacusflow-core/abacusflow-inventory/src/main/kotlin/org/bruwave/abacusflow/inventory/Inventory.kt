package org.bruwave.abacusflow.inventory

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import jakarta.validation.constraints.PositiveOrZero
import jdk.jfr.internal.SecuritySupport.registerEvent
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant

@Entity
@Table(name = "inventories")
class Inventory(
    val productId: Long,  // 通过ID关联商品

    warehouseId: Long?,
    quantity: Int = 0,
    reservedQuantity: Int = 0,
) : AbstractAggregateRoot<Inventory>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    var warehouseId: Long? = warehouseId  // 通过ID关联仓库
        private set

    @field:PositiveOrZero
    var quantity: Int = quantity // 当前库存
        private set

    @field:PositiveOrZero
    var reservedQuantity: Int = reservedQuantity // 被锁定、预留或冻结的库存数量
        private set

    @field:PositiveOrZero
    var safetyStock: Int = 1
        //安全库存量
        private set

    @field:PositiveOrZero
    var maxStock: Int = 10 //安全库存量
        private set

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
        require(amount <= MAX_ADJUSTMENT) { "每次减少的库存数量不能超过 $MAX_ADJUSTMENT 个" }
        quantity += amount
        updatedAt = Instant.now()
//        registerEvent(InventoryIncreasedEvent(id, productId, warehouseId, amount))
    }

    fun decreaseQuantity(amount: Int) {
        require(amount > 0) { "减少数量必须为正数" }
        require(amount <= MAX_ADJUSTMENT) { "每次减少的库存数量不能超过 $MAX_ADJUSTMENT 个" }
        require(quantity >= amount) { "库存不足" }
        quantity -= amount
        version++
        updatedAt = Instant.now()
//        registerEvent(InventoryDecreasedEvent(id, productId, warehouseId, amount))
    }

    fun reserveInventory(amount: Int) {
        require(amount > 0) { "冻结数量必须为正数" }
        require(amount <= MAX_ADJUSTMENT) { "每次冻结的库存数量不能超过 $MAX_ADJUSTMENT 个" }
        require(availableQuantity >= amount) { "可用库存不足，无法冻结 $amount 个库存" }

        reservedQuantity += amount
        updatedAt = Instant.now()

//        registerEvent(InventoryReservedEvent(id, productId, warehouseId, amount))
    }


    fun assignWarehouse(newWarehouseId: Long) {
        require(newWarehouseId > 0) { "无效的仓库ID" }

        if (this.warehouseId == newWarehouseId) return

        // 实际上需要将字段变为 var 才能赋值
        this.warehouseId = newWarehouseId
        this.updatedAt = Instant.now()

//        registerEvent(WarehouseAssignedEvent(id, productId, newWarehouseId))
    }

    fun adjustWarningLine(newSafetyStock: Int, newMaxStock: Int) {
        require(newSafetyStock >= 0) { "安全库存不能为负数" }
        require(newMaxStock >= newSafetyStock) { "最大库存必须大于或等于安全库存" }

        this.safetyStock = newSafetyStock
        this.maxStock = newMaxStock
        this.updatedAt = Instant.now()

//        registerEvent(WarningLineAdjustedEvent(id, productId, warehouseId, newSafetyStock, newMaxStock))
    }

    val isBelowSafetyStock
        get(): Boolean = quantity < safetyStock

    val availableQuantity
        get(): Int = quantity - reservedQuantity

    companion object {
        private const val MAX_ADJUSTMENT = 100
    }
}
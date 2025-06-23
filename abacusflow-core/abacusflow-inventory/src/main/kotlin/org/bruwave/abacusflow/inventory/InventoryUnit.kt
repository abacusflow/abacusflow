package org.bruwave.abacusflow.inventory

import jakarta.persistence.CollectionTable
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.ElementCollection
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.PositiveOrZero
import org.bruwave.abacusflow.inventory.Inventory
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.Type
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 或 JOINED
@DiscriminatorColumn(name = "unit_type", discriminatorType = DiscriminatorType.STRING)
abstract class InventoryUnit(
    @ManyToOne
    open val inventory: Inventory,
    open val purchaseOrderId: Long,
    // 冗余字段
    @field:PositiveOrZero
    open val quantity: Long,
    // 冗余字段
    open val unitPrice: Double,
    depotId: Long?
) {
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sale_order_ids", columnDefinition = "jsonb")
    private val saleOrderIdsMutable: MutableSet<Long> = mutableSetOf()

    val saleOrderIds: List<Long>
        get() = saleOrderIdsMutable.toList()

    open var depotId: Long? = depotId
        protected set

    open var remainingQuantity: Long = quantity
        protected set

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var status: InventoryUnitStatus = InventoryUnitStatus.NORMAL
        protected set

    @CreationTimestamp
    open val receivedAt: Instant = Instant.now()

    @CreationTimestamp
    open val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    open var updatedAt: Instant = Instant.now()
        protected set

    open fun consume(saleOrderId: Long, amount: Int) {
        require(amount > 0) { "销售数量必须为正数" }
        require(amount <= MAX_ADJUSTMENT) { "每次减少的库存数量不能超过 $MAX_ADJUSTMENT 个" }
        require(remainingQuantity >= amount) { "剩余库存不足" }
        require(status == InventoryUnitStatus.NORMAL) { "当前是未出库状态才可出库" }

        remainingQuantity -= amount
        saleOrderIdsMutable.add(saleOrderId)
        status = InventoryUnitStatus.CONSUMED

        updatedAt = Instant.now()
    }

    fun cancel(reason: String = "") {
        require(status == InventoryUnitStatus.NORMAL) { "只能取消未出库的库存" }
        status = InventoryUnitStatus.CANCELED
        updatedAt = Instant.now()
    }

    fun reverse() {
        require(status == InventoryUnitStatus.CONSUMED) { "只能撤销已出库的库存" }
        status = InventoryUnitStatus.REVERSED
        updatedAt = Instant.now()
    }

    fun assignDepot(newDepotId: Long) {
        require(newDepotId > 0) { "无效的仓库ID" }
        if (this.depotId == newDepotId) return

        this.depotId = newDepotId

        updatedAt = Instant.now()
    }

    @Entity
    @DiscriminatorValue("INSTANCE")
    class InstanceInventoryUnit(
        inventory: Inventory,
        purchaseOrderId: Long,
        depotId: Long?,
        unitPrice: Double,
        val serialNumber: String,
    ) : InventoryUnit(
        inventory = inventory,
        purchaseOrderId = purchaseOrderId,
        quantity = 1,
        depotId = depotId,
        unitPrice = unitPrice,
    ) {
        val inStock: Boolean
            get() = remainingQuantity == 1L

        override fun consume(saleOrderId: Long, amount: Int) {
            require(amount == 1) { "资产类产品每次只能出库 1 个" }
            require(remainingQuantity == 1L) { "资产产品已出库" }
            require(saleOrderIds.isEmpty()) { "资产类产品已绑定销售单" }

            super.consume(saleOrderId, amount)
        }
    }

    @Entity
    @DiscriminatorValue("BATCH")
    class BatchInventoryUnit(
        inventory: Inventory,
        purchaseOrderId: Long,
        quantity: Long,
        depotId: Long?,
        unitPrice: Double,
        val batchCode: UUID = UUID.randomUUID(),
    ) : InventoryUnit(
        inventory = inventory,
        purchaseOrderId = purchaseOrderId,
        quantity = quantity,
        unitPrice = unitPrice,
        depotId = depotId,
    ) {

    }

    enum class UnitType {
        INSTANCE,
        BATCH,
    }

    enum class InventoryUnitStatus {
        NORMAL,      // 初始状态，已入库未出库
        CONSUMED,    // 已出库
        CANCELED,    // 被人为取消
        REVERSED     // 因采购撤销等原因回退
    }

    companion object {
        private const val MAX_ADJUSTMENT = 100
    }
}
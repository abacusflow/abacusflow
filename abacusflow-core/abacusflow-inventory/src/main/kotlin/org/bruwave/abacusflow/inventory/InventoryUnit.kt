package org.bruwave.abacusflow.inventory

import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.DiscriminatorType
import jakarta.persistence.DiscriminatorValue
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
import jakarta.persistence.Version
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.JdbcType
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.dialect.PostgreSQLEnumJdbcType
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // 或 JOINED
@DiscriminatorColumn(name = "unit_type", discriminatorType = DiscriminatorType.STRING)
abstract class InventoryUnit(
    @ManyToOne
    @JoinColumn(name = "inventory_id", nullable = false)
    open val inventory: Inventory,
    open val purchaseOrderId: Long,
    // 初始库存
    @field:PositiveOrZero
    open val initialQuantity: Long,
    // 冗余字段
    open val unitPrice: BigDecimal,
    depotId: Long?,
) {
    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "sale_order_ids", columnDefinition = "bigint[]")
    private val saleOrderIdsMutable: MutableSet<Long> = mutableSetOf()

    val saleOrderIds: List<Long>
        get() = saleOrderIdsMutable.toList()

    open var depotId: Long? = depotId
        protected set

    // 当前库存
    @field:PositiveOrZero
    open var quantity: Long = initialQuantity
        protected set

    // 冻结库存
    @field:PositiveOrZero
    open var frozenQuantity: Long = 0
        protected set

    // 可用库存
    val remainingQuantity
        get() = quantity - frozenQuantity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open val id: Long = 0

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    @Column(nullable = false)
    open var status: InventoryUnitStatus = InventoryUnitStatus.NORMAL
        protected set

    @Version
    open var version: Long = 0
        protected set

    @CreationTimestamp
    open val receivedAt: Instant = Instant.now()

    @CreationTimestamp
    open val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    open var updatedAt: Instant = Instant.now()
        protected set

    open fun consume(
        saleOrderId: Long,
        amount: Int,
    ) {
        require(amount > 0) { "销售数量必须为正数" }
        require(amount <= MAX_ADJUSTMENT) { "每次减少的库存数量不能超过 $MAX_ADJUSTMENT 个" }
        require(remainingQuantity >= amount) { "剩余库存不足" }
        require(status in setOf(InventoryUnitStatus.NORMAL, InventoryUnitStatus.REVERSED)) {
            "当前是未出库或已退回状态才可出库"
        }
        quantity -= amount
        saleOrderIdsMutable.add(saleOrderId)

        if (remainingQuantity == 0L) {
            status = InventoryUnitStatus.CONSUMED
        }

        updatedAt = Instant.now()
    }

    open fun cancel(reason: String?) {
        require(remainingQuantity == quantity) { "只能取消尚未出库的库存" }

        status = InventoryUnitStatus.CANCELED
        updatedAt = Instant.now()
    }

    open fun reverse(
        amount: Int,
        saleOrderId: Long,
    ) {
        require(amount > 0) { "回退数量必须为正" }
        require(remainingQuantity < quantity) { "当前库存未出库，无需回退" }
        require(remainingQuantity + amount <= quantity) { "库存无法回退，超过原始数量" }

        quantity += amount
        saleOrderIdsMutable.remove(saleOrderId)

        status = InventoryUnitStatus.REVERSED

        updatedAt = Instant.now()
    }

    // 冻结库存
    open fun reserve(amount: Int) {
        require(amount > 0) { "冻结数量必须为正数" }
        require(amount <= remainingQuantity) { "冻结数量不能超过可用库存" }

        frozenQuantity += amount // 增加冻结库存

        updatedAt = Instant.now()
    }

    // 解冻库存
    open fun release(amount: Int) {
        require(amount > 0) { "解冻数量必须为正数" }
        require(amount <= frozenQuantity) { "解冻数量不能超过冻结库存" }

        frozenQuantity -= amount // 减少冻结库存

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
        unitPrice: BigDecimal,
        val serialNumber: String,
    ) : InventoryUnit(
            inventory = inventory,
            purchaseOrderId = purchaseOrderId,
            initialQuantity = 1,
            depotId = depotId,
            unitPrice = unitPrice,
        ) {
        val inStock: Boolean
            get() = remainingQuantity == 1L

        override fun consume(
            saleOrderId: Long,
            amount: Int,
        ) {
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
        initialQuantity: Long,
        depotId: Long?,
        unitPrice: BigDecimal,
        val batchCode: UUID,
    ) : InventoryUnit(
            inventory = inventory,
            purchaseOrderId = purchaseOrderId,
            initialQuantity = initialQuantity,
            unitPrice = unitPrice,
            depotId = depotId,
        )

    enum class UnitType {
        INSTANCE,
        BATCH,
    }

    enum class InventoryUnitStatus {
        NORMAL, // 初始状态，已入库未出库
        CONSUMED, // 已出库
        CANCELED, // 被人为取消
        REVERSED, // 因采购撤销等原因回退
    }

    companion object {
        private const val MAX_ADJUSTMENT = 100
    }
}

package org.bruwave.abacusflow.transaction

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

@Entity
@Table(name = "purchase_orders")
class PurchaseOrder(
    supplierId: Long,
    orderDate: LocalDate = LocalDate.now(),
    note: String?
) : AbstractAggregateRoot<PurchaseOrder>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @field:NotNull
    @Column(unique = true)
    val orderNo: UUID = UUID.randomUUID()

    var supplierId: Long = supplierId  // 通过ID关联供应商
        private set

    var status: OrderStatus = OrderStatus.PENDING
        private set

    var orderDate: LocalDate = orderDate
        private set

    var note: String? = note
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "order_id")
    val _items: MutableList<PurchaseOrderItem> = mutableListOf()

    val items: List<PurchaseOrderItem>
        get() = _items.toList()


    fun updateBasicInfo(newNote: String?) {
        newNote?.let {
            note = newNote
        }
    }

    fun changeOrderDate(newOrderDate: LocalDate?) {
        newOrderDate?.let {
            orderDate = newOrderDate
        }
    }

    fun changeSupplier(newSupplierId: Long) {
        if (supplierId == newSupplierId) return

        supplierId = newSupplierId
        updatedAt = Instant.now()
    }

    // TODO 最佳方案是替换为增量更新
    fun addItem(productId: Long, quantity: Int, unitPrice: Double) {
        _items.add(PurchaseOrderItem(productId, quantity, unitPrice))
        updatedAt = Instant.now()
    }

    fun completeOrder() {
        require(status == OrderStatus.PENDING) { "只有待处理订单才能完成" }
        status = OrderStatus.COMPLETED
        updatedAt = Instant.now()
        registerEvent(PurchaseOrderCompletedEvent(id, items.map {
            PurchaseOrderItem(it.productId, it.quantity, it.unitPrice)
        }))
    }

    fun clearItems() {
        _items.clear()
    }

    val totalAmount: Double
        get() = items.sumOf { it.subtotal }
    val totalQuantity: Long
        get() = items.sumOf { it.quantity.toLong() }
    val itemCount: Int
        get() = items.distinctBy { it.productId }.size
}


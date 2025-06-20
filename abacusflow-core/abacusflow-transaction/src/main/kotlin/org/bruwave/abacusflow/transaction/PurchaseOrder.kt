package org.bruwave.abacusflow.transaction

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.PrePersist
import jakarta.persistence.Table
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
    val supplierId: Long,
    val orderDate: LocalDate = LocalDate.now(),
    val note: String?,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "order_id")
    val items: List<PurchaseOrderItem>,
) : AbstractAggregateRoot<PurchaseOrder>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @field:NotNull
    @Column(unique = true)
    val no: UUID = UUID.randomUUID()

    var status: OrderStatus = OrderStatus.PENDING
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    fun completeOrder() {
        require(status == OrderStatus.PENDING) { "只有待处理订单才能完成" }
        status = OrderStatus.COMPLETED
        updatedAt = Instant.now()

        registerEvent(PurchaseOrderCompletedEvent(this))
    }

    fun cancelOrder() {
        require(status == OrderStatus.PENDING) { "只有待处理订单才能取消" }
        status = OrderStatus.CANCELED
        updatedAt = Instant.now()

        registerEvent(PurchaseOrderCanceledEvent(this))
    }

    val totalAmount: Double
        get() = items.sumOf { it.subtotal }
    val totalQuantity: Long
        get() = items.sumOf { it.quantity.toLong() }
    val itemCount: Int
        get() = items.distinctBy { it.productId }.size

    @PrePersist
    fun prePersist() {
        registerEvent(PurchaseOrderCreatedEvent(this))
    }
}

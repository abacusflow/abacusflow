package org.bruwave.abacusflow.transaction

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
@Table(name = "sale_orders")
class SaleOrder(
    val customerId: Long,
    val orderDate: LocalDate = LocalDate.now(),
    val note: String?,
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "order_id")
    val items: List<SaleOrderItem>,
) : AbstractAggregateRoot<SaleOrder>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @field:NotNull
    @Column(unique = true)
    val no: UUID = UUID.randomUUID()

    @Enumerated(EnumType.STRING)
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

        registerEvent(SaleOrderCompletedEvent(this))
    }

    fun cancelOrder() {
        require(status == OrderStatus.PENDING) { "只有待处理订单才能取消" }
        status = OrderStatus.CANCELED
        updatedAt = Instant.now()

        registerEvent(SaleOrderCanceledEvent(this))
    }


    fun reverseOrder() {
        require(status == OrderStatus.COMPLETED) { "只有已完成订单才能撤回" }
        status = OrderStatus.REVERSED
        updatedAt = Instant.now()

        registerEvent(SaleOrderReversedEvent(this))
    }

    val totalAmount: Double
        get() = items.sumOf { it.subtotal }
    val totalQuantity: Long
        get() = items.sumOf { it.quantity.toLong() }

    @PrePersist
    fun prePersist() {
        registerEvent(SaleOrderCreatedEvent(this))
    }
}

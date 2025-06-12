package org.bruwave.abacusflow.transaction

import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jdk.jfr.internal.SecuritySupport.registerEvent
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "purchase_orders")
class PurchaseOrder(
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id")
    val supplier: Supplier,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.PENDING
) : AbstractAggregateRoot<PurchaseOrder>() {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @NotNull
    val createdAt: Instant = Instant.EPOCH

    @UpdateTimestamp
    @NotNull
    var updatedAt: Instant = Instant.EPOCH
        private set

    @OneToMany(mappedBy = "purchaseOrder", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableSet<PurchaseItem> = mutableSetOf()

    fun addItem(product: Product, quantity: Int, unitPrice: Double) {
        val item = PurchaseItem(this, product, quantity, unitPrice)
        items.add(item)
        updatedAt = Instant.now()
    }

    fun completeOrder() {
        require(status == OrderStatus.PENDING) { "只有待处理订单才能完成" }
        status = OrderStatus.COMPLETED
        updatedAt = Instant.now()
        registerEvent(PurchaseOrderCompletedEvent(this))
    }

    fun cancelOrder() {
        require(status == OrderStatus.PENDING) { "只有待处理订单才能取消" }
        status = OrderStatus.CANCELLED
        updatedAt = Instant.now()
    }
}
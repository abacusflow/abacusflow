package org.bruwave.abacusflow.transaction

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jdk.internal.org.jline.utils.Colors.s
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant

@Entity
@Table(name = "purchase_orders")
class PurchaseOrder(
    supplierId: Long
) : AbstractAggregateRoot<PurchaseOrder>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    var supplierId: Long = supplierId  // 通过ID关联供应商
        private set

    var status: OrderStatus = OrderStatus.PENDING
        private set

    @CreationTimestamp
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    var updatedAt: Instant = Instant.now()
        private set

    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "order_id")
    val items: MutableList<PurchaseItem> = mutableListOf()

    fun changeSupplier(newSupplierId: Long) {
        if (this.supplierId == newSupplierId) return

        this.supplierId = newSupplierId
        this.updatedAt = Instant.now()
    }

    // TODO 最佳方案是替换为增量更新
    fun addItem(productId: Long, quantity: Int, unitPrice: Double) {
        items.add(PurchaseItem(productId, quantity, unitPrice))
        updatedAt = Instant.now()
    }

    fun completeOrder() {
        require(status == OrderStatus.PENDING) { "只有待处理订单才能完成" }
        status = OrderStatus.COMPLETED
        updatedAt = Instant.now()
        registerEvent(PurchaseOrderCompletedEvent(id, items.map {
            PurchaseItem(it.productId, it.quantity, it.unitPrice)
        }))
    }
}


package org.bruwave.abacusflow.transaction

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant

@Entity
@Table(name = "sale_orders")
class SaleOrder(
    customerId: Long
) : AbstractAggregateRoot<SaleOrder>() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    var customerId: Long = customerId // 通过ID关联客户
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
    val items: MutableList<SaleItem> = mutableListOf()

    fun changeCustomer(newCustomerId: Long) {
        if (this.customerId == newCustomerId) return

        this.customerId = newCustomerId
        this.updatedAt = Instant.now()
    }

    // TODO 最佳方案是替换为增量更新
    fun addItem(productId: Long, quantity: Int, unitPrice: Double) {
        items.add(SaleItem(productId, quantity, unitPrice))
        updatedAt = Instant.now()
    }

    fun completeOrder() {
        require(status == OrderStatus.PENDING) { "只有待处理订单才能完成" }
        status = OrderStatus.COMPLETED
        updatedAt = Instant.now()
        registerEvent(SaleOrderCompletedEvent(id, items.map {
            SaleItem(it.productId, it.quantity, it.unitPrice)
        }))
    }
}



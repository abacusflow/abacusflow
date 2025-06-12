package org.bruwave.abacusflow.transaction

import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
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
@Table(name = "sale_orders")
class SaleOrder(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    val customer: Customer? = null,

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = OrderStatus.PENDING
) : AbstractAggregateRoot<SaleOrder>() {

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

    @OneToMany(mappedBy = "saleOrder", cascade = [CascadeType.ALL], orphanRemoval = true)
    val items: MutableSet<SaleItem> = mutableSetOf()

    fun addItem(product: Product, quantity: Int, unitPrice: Double) {
        val item = SaleItem(this, product, quantity, unitPrice)
        items.add(item)
        updatedAt = Instant.now()
    }

    fun completeOrder() {
        require(status == OrderStatus.PENDING) { "只有待处理订单才能完成" }
        status = OrderStatus.COMPLETED
        updatedAt = Instant.now()
        registerEvent(SaleOrderCompletedEvent(this))
    }
}
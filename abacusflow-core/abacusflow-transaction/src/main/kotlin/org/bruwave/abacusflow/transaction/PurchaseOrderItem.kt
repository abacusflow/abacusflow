package org.bruwave.abacusflow.transaction

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero

@Entity
@Table(name = "purchase_order_items")
class PurchaseOrderItem(
    val productId: Long,
    @field:Positive
    val quantity: Int,
    @field:PositiveOrZero
    val unitPrice: Double,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    val subtotal: Double
        get() = unitPrice * quantity
}

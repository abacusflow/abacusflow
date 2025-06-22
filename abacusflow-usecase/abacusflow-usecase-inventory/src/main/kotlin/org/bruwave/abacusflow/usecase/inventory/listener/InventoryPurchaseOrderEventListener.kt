package org.bruwave.abacusflow.usecase.inventory.listener

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.transaction.PurchaseOrderCompletedEvent
import org.bruwave.abacusflow.transaction.PurchaseOrderReversedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class InventoryPurchaseOrderEventListener(
    private val inventoryRepository: InventoryRepository,
) {
    @EventListener
    fun handlePurchaseOrderCompletedEvent(event: PurchaseOrderCompletedEvent) {
        val order = event.order
        println("PurchaseOrder Completed orderNo: ${order.no}")

        order.items.groupBy { it.productId }.forEach {
            val inventory =
                inventoryRepository.findByProductId(it.key)
                    ?: throw NoSuchElementException("Product with id ${it.key} not found")

            val sumQuantity = it.value.sumOf { it.quantity }
            inventory.increaseQuantity(sumQuantity)
        }
    }

    @EventListener
    fun handlePurchaseOrderReversedEvent(event: PurchaseOrderReversedEvent) {
        val order = event.order
        println("PurchaseOrder Reversed orderNo: ${order.no}")
        order.items.groupBy { it.productId }.forEach {
            val inventory =
                inventoryRepository.findByProductId(it.key)
                    ?: throw NoSuchElementException("Product with id ${it.key} not found")

            val sumQuantity = it.value.sumOf { it.quantity }
            inventory.decreaseQuantity(sumQuantity)
        }
    }
}

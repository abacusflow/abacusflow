package org.bruwave.abacusflow.usecase.inventory.listener

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.inventory.Inventory
import org.bruwave.abacusflow.transaction.PurchaseOrderItemChangedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class PurchaseOrderEventListener(
    private val inventoryRepository: InventoryRepository,
) {

    @EventListener
    fun handlePurchaseOrderChanged(event: PurchaseOrderItemChangedEvent) {
        println("PurchaseOrder Changed orderNo: ${event.orderNo}")

        event.items.groupBy { it.productId }.forEach {
            val inventory = inventoryRepository.findByProductId(it.key)
                ?: throw NoSuchElementException("Product with id ${it.key} not found")

            val sumQuantity = it.value.sumOf { it.quantity }
            inventory.increaseQuantity(sumQuantity)
        }
    }
}
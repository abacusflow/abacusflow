package org.bruwave.abacusflow.usecase.inventory.listener

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.transaction.SaleOrderItemChangedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class SaleOrderEventListener(
    private val inventoryRepository: InventoryRepository,
) {

    @EventListener
    fun handleSaleOrderChanged(event: SaleOrderItemChangedEvent) {
        println("SaleOrder Changed orderNo: ${event.orderNo}")

        event.items.groupBy { it.productId }.forEach {
            val inventory = inventoryRepository.findByProductId(it.key)
                ?: throw NoSuchElementException("Product with id ${it.key} not found")

            val sumQuantity = it.value.sumOf { it.quantity }
            inventory.decreaseQuantity(sumQuantity)
        }
    }
}
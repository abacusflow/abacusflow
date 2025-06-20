package org.bruwave.abacusflow.usecase.inventory.listener

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.transaction.SaleOrderCompletedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class InventorySaleOrderEventListener(
    private val inventoryRepository: InventoryRepository,
) {
    @EventListener
    fun handleSaleOrderCompletedEvent(event: SaleOrderCompletedEvent) {
        val order = event.order
        println("SaleOrder Completed orderNo: ${order.no}")

        order.items.groupBy { it.productId }.forEach {
            val inventory =
                inventoryRepository.findByProductId(it.key)
                    ?: throw NoSuchElementException("Product with id ${it.key} not found")

            val sumQuantity = it.value.sumOf { it.quantity }
            inventory.decreaseQuantity(sumQuantity)
        }
    }
}

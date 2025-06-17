package org.bruwave.abacusflow.usecase.inventory.listener

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.inventory.Inventory
import org.bruwave.abacusflow.product.ProductCreatedEvent
import org.bruwave.abacusflow.product.ProductDeletedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class ProductEventListener(
    private val inventoryRepository: InventoryRepository,
) {
    @EventListener
    @Transactional
    fun handleProductCreated(event: ProductCreatedEvent) {
        println("New product created: ${event.product.id}, ${event.product.name}")

        inventoryRepository.save(
            Inventory(
                productId = event.product.id,
                warehouseId = null,
            ),
        )
    }

    @EventListener
    @Transactional
    fun handleProductDeleted(event: ProductDeletedEvent) {
        println("product deleted: ${event.product.id}, ${event.product.name}")

        val inventories = inventoryRepository.findByProductId(event.product.id)
        inventoryRepository.deleteAll(inventories)
    }
}

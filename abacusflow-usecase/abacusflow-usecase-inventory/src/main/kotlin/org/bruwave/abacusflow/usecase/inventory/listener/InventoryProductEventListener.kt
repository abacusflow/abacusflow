package org.bruwave.abacusflow.usecase.inventory.listener

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.inventory.Inventory
import org.bruwave.abacusflow.product.ProductCreatedEvent
import org.bruwave.abacusflow.product.ProductDeletedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class InventoryProductEventListener(
    private val inventoryRepository: InventoryRepository,
) {
    @EventListener
    fun handleProductCreated(event: ProductCreatedEvent) {
        println("New product created: ${event.product.id}, ${event.product.name}")

        inventoryRepository.save(
            Inventory(
                productId = event.product.id,
            ),
        )
    }

//    @EventListener
//    fun handleProductUpdated(event: ProductUpdatedEvent) {
//        println("product updated: ${event.product.id}, ${event.product.name}")
//    }

    @EventListener
    fun handleProductDeleted(event: ProductDeletedEvent) {
        println("product deleted: ${event.product.id}, ${event.product.name}")

        val inventory =
            inventoryRepository.findByProductId(event.product.id)
                ?: throw NoSuchElementException("Product with id ${event.product.id} not found")
        inventoryRepository.delete(inventory)
    }
}

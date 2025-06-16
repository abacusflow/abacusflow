package org.bruwave.abacusflow.usecase.inventory.listener

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.inventory.Inventory
import org.bruwave.abacusflow.product.ProductCreatedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ProductEventListener (
    private val inventoryRepository: InventoryRepository,
){

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun handleProductCreated(event: ProductCreatedEvent) {
        println("New product created: ${event.product.id}, ${event.product.name}")
        // 这里可以进一步发送通知、记录审计日志等

//        inventoryRepository.save(Inventory(
//            productId = event.productId,
//            warehouseId = null,
//        ))
    }
}
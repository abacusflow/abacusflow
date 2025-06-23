package org.bruwave.abacusflow.usecase.inventory.service.impl

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.db.inventory.InventoryUnitRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.inventory.Inventory
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.CreateInventoryInputTO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryCommandService
import org.bruwave.abacusflow.usecase.inventory.InventoryTO
import org.bruwave.abacusflow.usecase.inventory.toBasicTO
import org.bruwave.abacusflow.usecase.inventory.toTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InventoryCommandServiceImpl(
    private val inventoryRepository: InventoryRepository,
) : InventoryCommandService {
    override fun createInventory(input: CreateInventoryInputTO): InventoryTO {
        val inventory =
            Inventory(
                productId = input.productId,
                quantity = input.quantity,
            )
        return inventoryRepository.save(inventory).toTO()
    }

    override fun adjustWarningLine(
        id: Long,
        newSafetyStock: Long,
        newMaxStock: Long,
    ) {
        val inventory =
            inventoryRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Inventory not found") }
        inventory.adjustWarningLine(newSafetyStock, newMaxStock)
        inventoryRepository.save(inventory)
    }

    override fun checkSafetyStock(id: Long): Boolean {
        TODO("Not yet implemented")
    }
}

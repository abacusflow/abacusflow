package org.abacusflow.usecase.inventory.service.impl

import org.abacusflow.db.inventory.InventoryRepository
import org.abacusflow.inventory.Inventory
import org.abacusflow.usecase.inventory.CreateInventoryInputTO
import org.abacusflow.usecase.inventory.InventoryTO
import org.abacusflow.usecase.inventory.mapper.toTO
import org.abacusflow.usecase.inventory.service.InventoryCommandService
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

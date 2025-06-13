package org.bruwave.abacusflow.usecase.inventory.impl

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.inventory.Inventory
import org.bruwave.abacusflow.usecase.inventory.InventoryService
import org.bruwave.abacusflow.usecase.inventory.InventoryTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InventoryServiceImpl(
    private val inventoryRepository: InventoryRepository,
) : InventoryService {

    override fun createInventory(to: InventoryTO): InventoryTO {
        val inventory = Inventory(
            productId = to.productId,
            warehouseId = to.warehouseId,
            quantity = to.quantity,
            safetyStock = to.safetyStock
        )
        return inventoryRepository.save(inventory).toTO()
    }

    override fun increaseInventory(id: Long, amount: Int): InventoryTO {
        val inventory = inventoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Inventory not found") }
        inventory.increaseQuantity(amount)
        return inventoryRepository.save(inventory).toTO()
    }

    override fun decreaseInventory(id: Long, amount: Int): InventoryTO {
        val inventory = inventoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Inventory not found") }
        inventory.decreaseQuantity(amount)
        return inventoryRepository.save(inventory).toTO()
    }

    override fun getInventory(id: Long): InventoryTO {
        return inventoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Inventory not found") }
            .toTO()
    }

    override fun listInventories(): List<InventoryTO> {
        return inventoryRepository.findAll().map { it.toTO() }
    }

    private fun Inventory.toTO(): InventoryTO = InventoryTO(
        id = id,
        productId = productId,
        warehouseId = warehouseId,
        quantity = quantity,
        safetyStock = safetyStock,
        version = version,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

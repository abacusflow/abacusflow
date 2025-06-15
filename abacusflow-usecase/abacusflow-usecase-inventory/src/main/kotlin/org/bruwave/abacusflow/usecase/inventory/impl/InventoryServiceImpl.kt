package org.bruwave.abacusflow.usecase.inventory.impl

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.warehouse.WarehouseRepository
import org.bruwave.abacusflow.inventory.Inventory
import org.bruwave.abacusflow.usecase.inventory.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InventoriesServiceImpl(
    private val inventoryRepository: InventoryRepository,
    private val productRepository: ProductRepository,
    private val warehouseRepository: WarehouseRepository
) : InventoriesService {

    override fun createInventory(input: CreateInventoryInputTO): InventoryTO {
        val inventory = Inventory(
            productId = input.productId,
            warehouseId = input.warehouseId,
            quantity = input.quantity,
            safetyStock = input.safetyStock,
            maxStock = input.maxStock
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
            .orElseThrow { NoSuchElementException("Inventory not found with id: $id") }
            .toTO()
    }

    override fun listInventories(): List<BasicInventoryTO> {
        val inventories = inventoryRepository.findAll()

        // 批量获取所有涉及的产品和仓库
        val productMap = productRepository.findAllById(inventories.map { it.productId }).associateBy { it.id }
        val warehouseMap = warehouseRepository.findAllById(inventories.map { it.warehouseId }).associateBy { it.id }

        return inventories.map { inventory ->
            val productName = productMap[inventory.productId]?.name ?: "未知产品"
            val warehouseName = warehouseMap[inventory.warehouseId]?.name ?: "未知仓库"
            inventory.toBasicTO(productName, warehouseName)
        }
    }

    override fun checkSafetyStock(id: Long): Boolean {
        val inventory = inventoryRepository.findById(id)
            .orElseThrow { NoSuchElementException("Inventory not found with id: $id") }

        return inventory.isBelowSafetyStock()
    }
}


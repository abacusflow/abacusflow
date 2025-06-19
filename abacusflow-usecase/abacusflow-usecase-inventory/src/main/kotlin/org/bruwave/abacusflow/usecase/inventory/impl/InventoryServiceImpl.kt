package org.bruwave.abacusflow.usecase.inventory.impl

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.depot.DepotRepository
import org.bruwave.abacusflow.inventory.Inventory
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.CreateInventoryInputTO
import org.bruwave.abacusflow.usecase.inventory.InventoryService
import org.bruwave.abacusflow.usecase.inventory.InventoryTO
import org.bruwave.abacusflow.usecase.inventory.toBasicTO
import org.bruwave.abacusflow.usecase.inventory.toTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InventoryServiceImpl(
    private val inventoryRepository: InventoryRepository,
    private val productRepository: ProductRepository,
    private val depotRepository: DepotRepository,
) : InventoryService {
    override fun createInventory(input: CreateInventoryInputTO): InventoryTO {
        val inventory =
            Inventory(
                productId = input.productId,
                depotId = input.depotId,
                quantity = input.quantity,
            )
        return inventoryRepository.save(inventory).toTO()
    }

    override fun increaseInventory(
        id: Long,
        amount: Int,
    ) {
        val inventory =
            inventoryRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Inventory not found") }
        inventory.increaseQuantity(amount)
        inventoryRepository.save(inventory)
    }

    override fun decreaseInventory(
        id: Long,
        amount: Int,
    ) {
        val inventory =
            inventoryRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Inventory not found") }
        inventory.decreaseQuantity(amount)
        inventoryRepository.save(inventory)
    }

    override fun reserveInventory(
        id: Long,
        amount: Int,
    ) {
        val inventory =
            inventoryRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Inventory not found") }
        inventory.reserveInventory(amount)
        inventoryRepository.save(inventory)
    }

    override fun assignDepot(
        id: Long,
        newDepotId: Long,
    ) {
        val inventory =
            inventoryRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Inventory not found") }
        inventory.assignDepot(newDepotId)
        inventoryRepository.save(inventory)
    }

    override fun adjustWarningLine(
        id: Long,
        newSafetyStock: Int,
        newMaxStock: Int,
    ) {
        val inventory =
            inventoryRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Inventory not found") }
        inventory.adjustWarningLine(newSafetyStock, newMaxStock)
        inventoryRepository.save(inventory)
    }

    override fun getInventory(id: Long): InventoryTO =
        inventoryRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Inventory not found with id: $id") }
            .toTO()

    override fun listInventories(): List<BasicInventoryTO> {
        val inventories = inventoryRepository.findAll()

        val productIds = inventories.mapNotNull { it.productId }.toSet()
        val depotIds = inventories.mapNotNull { it.depotId }.toSet()

        // 批量获取所有涉及的产品和仓库
        val productMap = productRepository.findAllById(productIds).associateBy { it.id }
        val depotMap = depotRepository.findAllById(depotIds).associateBy { it.id }

        return inventories.map { inventory ->
            val productName = productMap[inventory.productId]?.name ?: "unknown"
            val depotName = depotMap[inventory.depotId]?.name ?: "unknown"
            inventory.toBasicTO(productName, depotName)
        }
    }

    override fun checkSafetyStock(id: Long): Boolean {
        val inventory =
            inventoryRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Inventory not found with id: $id") }

        return inventory.isBelowSafetyStock
    }
}

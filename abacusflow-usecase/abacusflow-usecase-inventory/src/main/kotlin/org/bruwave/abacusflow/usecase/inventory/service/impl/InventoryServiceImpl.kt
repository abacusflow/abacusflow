package org.bruwave.abacusflow.usecase.inventory.service.impl

import org.bruwave.abacusflow.db.depot.DepotRepository
import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.db.inventory.InventoryUnitRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderItemRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderItemRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.inventory.Inventory
import org.bruwave.abacusflow.transaction.OrderStatus
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.CreateInventoryInputTO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryService
import org.bruwave.abacusflow.usecase.inventory.InventoryTO
import org.bruwave.abacusflow.usecase.inventory.toBasicTO
import org.bruwave.abacusflow.usecase.inventory.toTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InventoryServiceImpl(
    private val inventoryRepository: InventoryRepository,
    private val inventoryUnitRepository: InventoryUnitRepository,
    private val productRepository: ProductRepository,
    private val depotRepository: DepotRepository,
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val purchaseOrderItemRepository: PurchaseOrderItemRepository,
    private val saleOrderRepository: SaleOrderRepository,
    private val saleOrderItemRepository: SaleOrderItemRepository,
) : InventoryService {
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

    override fun getInventory(id: Long): InventoryTO =
        inventoryRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Inventory not found with id: $id") }
            .toTO()

    override fun listInventories(): List<BasicInventoryTO> {
        val inventories = inventoryRepository.findAll()
        val inventoryUnits = inventoryUnitRepository.findAll()
        val purchaseOrderIds = inventoryUnits.mapNotNull { it.purchaseOrderId }.toSet()
        val saleOrderIds = inventoryUnits.flatMap { it.saleOrderIds }.toSet()
        val productIds = inventories.mapNotNull { it.productId }.toSet()

        val productsById = productRepository.findAllById(productIds)
        val purchaseOrdersById = purchaseOrderRepository.findAllById(purchaseOrderIds)
        val saleOrdersById = saleOrderRepository.findAllById(saleOrderIds)

        val productMap = productsById.associateBy { it.id }
        val purchaseOrdersMap = purchaseOrdersById.associateBy { it.id }
        val saleOrdersMap = saleOrdersById.associateBy { it.id }
        val inventoryUnitMap = inventoryUnits.groupBy { it.inventory.id }

        return inventories.map { inventory ->
            val product = productMap.getValue(inventory.productId)
            val units = inventoryUnitMap[inventory.id] ?: listOf()

            val unitTOS = units.map {
                val purchaseOrder = purchaseOrdersMap.getValue(it.purchaseOrderId)
                val saleOrders = it.saleOrderIds.map { saleOrderId -> saleOrdersMap.getValue(saleOrderId) }
                it.toBasicTO(purchaseOrder.no, saleOrders.map { it.no })
            }
            inventory.toBasicTO(product.name, unitTOS)
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

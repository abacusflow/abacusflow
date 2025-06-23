package org.bruwave.abacusflow.usecase.inventory.service.impl

import org.bruwave.abacusflow.db.inventory.InventoryUnitRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderItemRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryService
import org.bruwave.abacusflow.usecase.inventory.service.InventoryUnitService
import org.bruwave.abacusflow.usecase.inventory.toBasicTO
import org.springframework.stereotype.Service
import kotlin.collections.map

@Service
class InventoryUnitServiceImpl(
    private val inventoryUnitRepository: InventoryUnitRepository,
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val saleOrderRepository: SaleOrderRepository,
    private val productRepository: ProductRepository,
) : InventoryUnitService {
    override fun listInventoryUnits(): List<BasicInventoryUnitTO> {
        val inventoryUnits = inventoryUnitRepository.findAll()
        val purchaseOrderIds = inventoryUnits.mapNotNull { it.purchaseOrderId }.toSet()
        val saleOrderIds = inventoryUnits.flatMap { it.saleOrderIds }.toSet()
        val productIds = inventoryUnits.mapNotNull { it.inventory.productId }.toSet()

        val productsById = productRepository.findAllById(productIds)
        val purchaseOrdersById = purchaseOrderRepository.findAllById(purchaseOrderIds)
        val saleOrdersById = saleOrderRepository.findAllById(saleOrderIds)

        val productMap = productsById.associateBy { it.id }

        val purchaseOrdersMap = purchaseOrdersById.associateBy { it.id }
        val saleOrdersMap = saleOrdersById.associateBy { it.id }

        return inventoryUnits.map {
            val purchaseOrder = purchaseOrdersMap.getValue(it.purchaseOrderId)
            val saleOrders = it.saleOrderIds.map { saleOrderId -> saleOrdersMap.getValue(saleOrderId) }
            it.toBasicTO(purchaseOrder.no, saleOrders.map { it.no })
        }
    }
}
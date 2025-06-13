package org.bruwave.abacusflow.usecase.transaction.impl

import org.bruwave.abacusflow.db.PurchaseOrderRepository
import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.bruwave.abacusflow.transaction.PurchaseItem
import org.bruwave.abacusflow.transaction.OrderStatus
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderService
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseItemTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PurchaseOrderServiceImpl(
    private val purchaseOrderRepository: PurchaseOrderRepository,
) : PurchaseOrderService {
    override fun createPurchaseOrder(order: PurchaseOrderTO): PurchaseOrderTO {
        val purchaseOrder = PurchaseOrder(
            supplierId = order.supplierId,
            status = OrderStatus.valueOf(order.status)
        )
        order.items.forEach {
            purchaseOrder.addItem(it.productId, it.quantity, it.unitPrice)
        }
        return purchaseOrderRepository.save(purchaseOrder).toPurchaseOrderTO()
    }

    override fun updatePurchaseOrder(orderTO: PurchaseOrderTO): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository.findById(orderTO.id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
        purchaseOrder.status = OrderStatus.valueOf(orderTO.status)
        return purchaseOrderRepository.save(purchaseOrder).toPurchaseOrderTO()
    }

    override fun deletePurchaseOrder(orderTO: PurchaseOrderTO): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository.findById(orderTO.id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
        purchaseOrderRepository.delete(purchaseOrder)
        return orderTO
    }

    override fun getPurchaseOrder(id: Long): PurchaseOrderTO {
        return purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
            .toPurchaseOrderTO()
    }

    override fun listPurchaseOrdersBySupplier(supplierId: Long): List<PurchaseOrderTO> {
        return purchaseOrderRepository.findBySupplierId(supplierId).map { it.toPurchaseOrderTO() }
    }

    override fun listPurchaseOrders(): List<PurchaseOrderTO> {
        return purchaseOrderRepository.findAll().map { it.toPurchaseOrderTO() }
    }

    override fun completeOrder(id: Long): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
        purchaseOrder.completeOrder()
        return purchaseOrderRepository.save(purchaseOrder).toPurchaseOrderTO()
    }

    private fun PurchaseOrder.toPurchaseOrderTO() = PurchaseOrderTO(
        id = id,
        supplierId = supplierId,
        status = status.name,
        items = items.map { it.toPurchaseItemTO() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun PurchaseItem.toPurchaseItemTO() = PurchaseItemTO(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice
    )
} 
package org.bruwave.abacusflow.usecase.transaction.impl

import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.transaction.PurchaseOrderItem
import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.bruwave.abacusflow.usecase.transaction.BasicPurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreatePurchaseOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderItemTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderService
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.UpdatePurchaseOrderInputTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PurchaseOrderServiceImpl(
    private val purchaseOrderRepository: PurchaseOrderRepository,
) : PurchaseOrderService {

    override fun createPurchaseOrder(input: CreatePurchaseOrderInputTO): PurchaseOrderTO {
        val purchaseOrder = PurchaseOrder(
            supplierId = input.supplierId,
        )
        input.orderItems.forEach {
            purchaseOrder.addItem(it.productId, it.quantity, it.unitPrice)
        }
        return purchaseOrderRepository.save(purchaseOrder).toTO()
    }

    override fun updatePurchaseOrder(id: Long, input: UpdatePurchaseOrderInputTO): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }

        purchaseOrder.apply {
            input.supplierId?.let {
                changeSupplier(it)
            }

            clearItems()

            input.orderItems?.forEach {
                purchaseOrder.addItem(it.productId, it.quantity, it.unitPrice)
            }
        }

        return purchaseOrderRepository.save(purchaseOrder).toTO()
    }

    override fun deletePurchaseOrder(id: Long): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
        purchaseOrderRepository.delete(purchaseOrder)
        return purchaseOrder.toTO()
    }

    override fun getPurchaseOrder(id: Long): PurchaseOrderTO {
        return purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
            .toTO()
    }

    override fun listPurchaseOrdersBySupplier(supplierId: Long): List<BasicPurchaseOrderTO> {
        return purchaseOrderRepository.findBySupplierId(supplierId).map { it.toBasicTO() }
    }

    override fun listPurchaseOrders(): List<BasicPurchaseOrderTO> {
        return purchaseOrderRepository.findAll().map { it.toBasicTO() }
    }

    override fun completeOrder(id: Long): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
        purchaseOrder.completeOrder()
        return purchaseOrderRepository.save(purchaseOrder).toTO()
    }

    private fun PurchaseOrder.toTO() = PurchaseOrderTO(
        id = id,
        supplierId = supplierId,
        status = status.name,
        items = items.map { it.toTO() },
        createdAt = createdAt,
        updatedAt = updatedAt,
        orderNo = orderNo
    )

    private fun PurchaseOrder.toBasicTO() = BasicPurchaseOrderTO(
        id = id,
        supplierName = "null",//TODO-NULL
        status = status.name,
        itemCount = items.size,
        createdAt = createdAt,
        orderNo = orderNo,
        totalAmount = totalAmount,
        totalQuantity = totalQuantity,
        orderDate = orderDate,
    )

    private fun PurchaseOrderItem.toTO() = PurchaseOrderItemTO(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice,
        subtotal = subtotal
    )
} 
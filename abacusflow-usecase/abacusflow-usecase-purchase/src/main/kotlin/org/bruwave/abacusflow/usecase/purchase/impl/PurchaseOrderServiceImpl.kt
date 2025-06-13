package org.bruwave.abacusflow.usecase.purchase.impl

import org.bruwave.abacusflow.db.purchase.PurchaseOrderRepository
import org.bruwave.abacusflow.purchase.PurchaseOrder
import org.bruwave.abacusflow.usecase.purchase.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PurchaseOrderServiceImpl(
    private val purchaseOrderRepository: PurchaseOrderRepository
) : PurchaseOrderService {

    override fun createPurchaseOrder(input: CreatePurchaseOrderInputTO): PurchaseOrderTO {
        val purchaseOrder = PurchaseOrder(
            supplierId = input.supplierId,
            orderNumber = input.orderNumber,
            orderDate = input.orderDate,
            status = input.status,
            totalAmount = input.totalAmount,
            paymentStatus = input.paymentStatus,
            shippingStatus = input.shippingStatus,
            notes = input.notes
        )
        return purchaseOrderRepository.save(purchaseOrder).toPurchaseOrderTO()
    }

    override fun updatePurchaseOrder(id: Long, input: UpdatePurchaseOrderInputTO): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Purchase order not found with id: $id") }
        
        purchaseOrder.apply {
            status = input.status
            totalAmount = input.totalAmount
            paymentStatus = input.paymentStatus
            shippingStatus = input.shippingStatus
            notes = input.notes
        }
        
        return purchaseOrderRepository.save(purchaseOrder).toPurchaseOrderTO()
    }

    override fun deletePurchaseOrder(id: Long): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Purchase order not found with id: $id") }
        
        purchaseOrderRepository.delete(purchaseOrder)
        return purchaseOrder.toPurchaseOrderTO()
    }

    override fun getPurchaseOrder(id: Long): PurchaseOrderTO {
        return purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Purchase order not found with id: $id") }
            .toPurchaseOrderTO()
    }

    override fun listPurchaseOrders(): List<BasicPurchaseOrderTO> {
        return purchaseOrderRepository.findAll().map { it.toBasicPurchaseOrderTO() }
    }

    override fun listPurchaseOrdersBySupplier(supplierId: Long): List<BasicPurchaseOrderTO> {
        return purchaseOrderRepository.findBySupplierId(supplierId).map { it.toBasicPurchaseOrderTO() }
    }

    override fun updateOrderStatus(id: Long, status: String): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Purchase order not found with id: $id") }
        
        purchaseOrder.status = status
        return purchaseOrderRepository.save(purchaseOrder).toPurchaseOrderTO()
    }

    override fun updatePaymentStatus(id: Long, status: String): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Purchase order not found with id: $id") }
        
        purchaseOrder.paymentStatus = status
        return purchaseOrderRepository.save(purchaseOrder).toPurchaseOrderTO()
    }

    override fun updateShippingStatus(id: Long, status: String): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Purchase order not found with id: $id") }
        
        purchaseOrder.shippingStatus = status
        return purchaseOrderRepository.save(purchaseOrder).toPurchaseOrderTO()
    }
}

private fun PurchaseOrder.toPurchaseOrderTO() = PurchaseOrderTO(
    id = id,
    supplierId = supplierId,
    orderNumber = orderNumber,
    orderDate = orderDate,
    status = status,
    totalAmount = totalAmount,
    paymentStatus = paymentStatus,
    shippingStatus = shippingStatus,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt
)

private fun PurchaseOrder.toBasicPurchaseOrderTO() = BasicPurchaseOrderTO(
    id = id,
    supplierId = supplierId,
    orderNumber = orderNumber,
    orderDate = orderDate,
    status = status,
    totalAmount = totalAmount,
    paymentStatus = paymentStatus,
    shippingStatus = shippingStatus
) 
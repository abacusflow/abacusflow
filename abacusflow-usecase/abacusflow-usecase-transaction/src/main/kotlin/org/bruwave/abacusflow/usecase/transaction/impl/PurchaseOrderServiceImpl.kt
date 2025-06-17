package org.bruwave.abacusflow.usecase.transaction.impl

import org.bruwave.abacusflow.db.partner.SupplierRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.bruwave.abacusflow.usecase.transaction.BasicPurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreatePurchaseOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderService
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.UpdatePurchaseOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PurchaseOrderServiceImpl(
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val supplierRepository: SupplierRepository,
) : PurchaseOrderService {
    override fun createPurchaseOrder(input: CreatePurchaseOrderInputTO): PurchaseOrderTO {
        val purchaseOrder =
            PurchaseOrder(
                supplierId = input.supplierId,
                orderDate = input.orderDate,
                note = input.note,
            )
        input.orderItems.forEach {
            purchaseOrder.addItem(it.productId, it.quantity, it.unitPrice)
        }
        return purchaseOrderRepository.save(purchaseOrder).toTO()
    }

    override fun updatePurchaseOrder(
        id: Long,
        input: UpdatePurchaseOrderInputTO,
    ): PurchaseOrderTO {
        val purchaseOrder =
            purchaseOrderRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("PurchaseOrder not found") }

        purchaseOrder.apply {
            input.supplierId?.let {
                changeSupplier(it)
            }

            input.orderDate?.let {
                changeOrderDate(it)
            }

            clearItems()

            input.orderItems?.forEach {
                purchaseOrder.addItem(it.productId, it.quantity, it.unitPrice)
            }
        }

        return purchaseOrderRepository.save(purchaseOrder).toTO()
    }

    override fun deletePurchaseOrder(id: Long): PurchaseOrderTO {
        val purchaseOrder =
            purchaseOrderRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
        purchaseOrderRepository.delete(purchaseOrder)
        return purchaseOrder.toTO()
    }

    override fun getPurchaseOrder(id: Long): PurchaseOrderTO =
        purchaseOrderRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
            .toTO()

    override fun listPurchaseOrdersBySupplier(supplierId: Long): List<BasicPurchaseOrderTO> {
        val supplier =
            supplierRepository
                .findById(supplierId)
                .orElseThrow { NoSuchElementException("Supplier not found") }

        return purchaseOrderRepository.findBySupplierId(supplierId).map { it.toBasicTO(supplier.name) }
    }

    override fun listPurchaseOrders(): List<BasicPurchaseOrderTO> {
        val orders = purchaseOrderRepository.findAll()
        val supplierIds = orders.mapNotNull { it.supplierId }.toSet()
        val supplierMap = supplierRepository.findAllById(supplierIds).associateBy { it.id }

        return orders.map { order ->
            val supplierName = supplierMap[order.supplierId]?.name ?: "unknown"

            order.toBasicTO(supplierName)
        }
    }

    override fun completeOrder(id: Long): PurchaseOrderTO {
        val purchaseOrder =
            purchaseOrderRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
        purchaseOrder.completeOrder()
        return purchaseOrderRepository.save(purchaseOrder).toTO()
    }
}

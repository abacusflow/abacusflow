package org.bruwave.abacusflow.usecase.transaction.impl

import org.bruwave.abacusflow.db.partner.SupplierRepository
import org.bruwave.abacusflow.db.product.ProductInstanceRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.product.ProductInstance
import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.bruwave.abacusflow.transaction.PurchaseOrderItem
import org.bruwave.abacusflow.transaction.TransactionProductType
import org.bruwave.abacusflow.usecase.transaction.BasicPurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreatePurchaseOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseItemInputTO
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
    private val productRepository: ProductRepository,
) : PurchaseOrderService {
    override fun createPurchaseOrder(input: CreatePurchaseOrderInputTO): PurchaseOrderTO {
        val products = productRepository.findAllById(
            input.orderItems
                .map { it.productId }
                .distinct()
        )
        val productMapById = products.associateBy { it.id }

        val purchaseOrder = PurchaseOrder(
            supplierId = input.supplierId,
            orderDate = input.orderDate,
            note = input.note,
        ).apply {
            val orderItems = mapInputOrderItemToOrderItem(input.orderItems, productMapById);
            addItems(orderItems)
        }

        return purchaseOrderRepository.save(purchaseOrder).toTO()
    }

    override fun updatePurchaseOrder(
        id: Long,
        input: UpdatePurchaseOrderInputTO,
    ): PurchaseOrderTO {
        val purchaseOrder = purchaseOrderRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }

        purchaseOrder.apply {
            input.supplierId?.let {
                changeSupplier(it)
            }

            input.orderDate?.let {
                changeOrderDate(it)
            }

            input.orderItems?.let {
                clearItems()

                val products = productRepository.findAllById(
                    it
                        .map { it.productId }
                        .distinct()
                )
                val productMapById = products.associateBy { it.id }

                val orderItems = mapInputOrderItemToOrderItem(input.orderItems, productMapById);
                addItems(orderItems)
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


    private fun mapInputOrderItemToOrderItem(
        orderItemsForInput: List<PurchaseItemInputTO>,
        productMapById: Map<Long, Product>
    ): List<PurchaseOrder.PurchaseOrderItemInput> {
        return orderItemsForInput.map { item ->
            val product = productMapById.getValue(item.productId)

            when (product.type) {
                Product.ProductType.MATERIAL -> PurchaseOrder.PurchaseOrderItemInput(
                    item.productId,
                    TransactionProductType.MATERIAL,
                    item.quantity,
                    item.unitPrice,
                    serialNumber = null
                )

                Product.ProductType.ASSET -> {
                    requireNotNull(item.serialNumber) { "asset item's serial number must not be null" }
                    PurchaseOrder.PurchaseOrderItemInput(
                        item.productId,
                        TransactionProductType.ASSET,
                        1,
                        item.unitPrice,
                        serialNumber = item.serialNumber
                    )
                }
            }
        }
    }
}

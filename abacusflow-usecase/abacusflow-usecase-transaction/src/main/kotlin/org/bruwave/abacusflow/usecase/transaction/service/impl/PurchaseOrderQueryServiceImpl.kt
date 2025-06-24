package org.bruwave.abacusflow.usecase.transaction.service.impl

import org.bruwave.abacusflow.db.partner.SupplierRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.bruwave.abacusflow.transaction.PurchaseOrderItem
import org.bruwave.abacusflow.transaction.TransactionProductType
import org.bruwave.abacusflow.usecase.transaction.BasicPurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreatePurchaseOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseItemInputTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toTO
import org.bruwave.abacusflow.usecase.transaction.service.PurchaseOrderCommandService
import org.bruwave.abacusflow.usecase.transaction.service.PurchaseOrderQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class PurchaseOrderQueryServiceImpl(
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val supplierRepository: SupplierRepository,
    private val productRepository: ProductRepository,
) : PurchaseOrderQueryService {
    override fun listPurchaseOrders(): List<BasicPurchaseOrderTO> {
        val orders = purchaseOrderRepository.findAll()
        val supplierIds = orders.mapNotNull { it.supplierId }.toSet()
        val supplierMap = supplierRepository.findAllById(supplierIds).associateBy { it.id }

        return orders.map { order ->
            val supplierName = supplierMap[order.supplierId]?.name ?: "unknown"

            order.toBasicTO(supplierName)
        }
    }

    override fun getPurchaseOrder(id: Long): PurchaseOrderTO =
        purchaseOrderRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("PurchaseOrder not found") }
            .toTO()
}

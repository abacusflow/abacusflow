package org.bruwave.abacusflow.usecase.transaction.service.impl

import org.bruwave.abacusflow.db.partner.CustomerRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.transaction.SaleOrder
import org.bruwave.abacusflow.transaction.SaleOrderItem
import org.bruwave.abacusflow.transaction.TransactionProductType
import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.SaleItemInputTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toTO
import org.bruwave.abacusflow.usecase.transaction.service.SaleOrderService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.collections.map

@Service
@Transactional
class SaleOrderServiceImpl(
    private val saleOrderRepository: SaleOrderRepository,
    private val customerRepository: CustomerRepository,
    private val productRepository: ProductRepository,
) : SaleOrderService {
    override fun createSaleOrder(input: CreateSaleOrderInputTO): SaleOrderTO {
        val products =
            productRepository.findAllById(
                input.orderItems
                    .map { it.productId }
                    .distinct(),
            )
        val productMapById = products.associateBy { it.id }

        val orderItems =
            input.orderItems.map { item ->
                mapInputOrderItemToOrderItem(item, productMapById)
            }

        val saleOrder =
            SaleOrder(
                customerId = input.customerId,
                orderDate = input.orderDate,
                note = input.note,
                items = orderItems,
            )

        return saleOrderRepository.save(saleOrder).toTO()
    }

    override fun getSaleOrder(id: Long): SaleOrderTO =
        saleOrderRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }
            .toTO()

    override fun listSaleOrders(): List<BasicSaleOrderTO> {
        val oreders = saleOrderRepository.findAll()
        val customerIds = oreders.mapNotNull { it.customerId }.toSet()
        val customerMap = customerRepository.findAllById(customerIds).associateBy { it.id }

        return oreders.map { order ->
            val supplierName = customerMap[order.customerId]?.name ?: "unknown"

            order.toBasicTO(supplierName)
        }
    }

    override fun completeOrder(id: Long): SaleOrderTO {
        val saleOrder =
            saleOrderRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("SaleOrder not found") }
        saleOrder.completeOrder()
        return saleOrderRepository.save(saleOrder).toTO()
    }

    override fun cancelOrder(id: Long): SaleOrderTO {
        val saleOrder =
            saleOrderRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("SaleOrder not found") }
        saleOrder.cancelOrder()
        return saleOrderRepository.save(saleOrder).toTO()
    }

    override fun reverseOrder(id: Long): SaleOrderTO {
        val saleOrder =
            saleOrderRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("SaleOrder not found") }
        saleOrder.reverseOrder()
        return saleOrderRepository.save(saleOrder).toTO()
    }

    private fun mapInputOrderItemToOrderItem(
        item: SaleItemInputTO,
        productMapById: Map<Long, Product>,
    ): SaleOrderItem {
        val product = productMapById.getValue(item.productId)

        return when (product.type) {
            Product.ProductType.MATERIAL ->
                SaleOrderItem(
                    item.productId,
                    TransactionProductType.MATERIAL,
                    item.quantity,
                    item.unitPrice,
                    productInstanceId = null,
                )

            Product.ProductType.ASSET -> {
                requireNotNull(item.productInstanceId) { "asset item's productInstanceId must not be null" }
                SaleOrderItem(
                    item.productId,
                    TransactionProductType.ASSET,
                    1, // 资产类固定数量为1
                    item.unitPrice,
                    productInstanceId = item.productInstanceId,
                )
            }
        }
    }
}

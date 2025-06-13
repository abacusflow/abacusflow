package org.bruwave.abacusflow.usecase.transaction.impl

import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.transaction.OrderStatus
import org.bruwave.abacusflow.transaction.SaleItem
import org.bruwave.abacusflow.transaction.SaleOrder
import org.bruwave.abacusflow.usecase.transaction.SaleItemTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderService
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SaleOrderServiceImpl(
    private val saleOrderRepository: SaleOrderRepository,
) : SaleOrderService {
    override fun createSaleOrder(order: SaleOrderTO): SaleOrderTO {
        val saleOrder = SaleOrder(
            customerId = order.customerId,
            status = OrderStatus.valueOf(order.status)
        )
        order.items.forEach {
            saleOrder.addItem(it.productId, it.quantity, it.unitPrice)
        }
        return saleOrderRepository.save(saleOrder).toSaleOrderTO()
    }

    override fun updateSaleOrder(orderTO: SaleOrderTO): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(orderTO.id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }
        // 这里只更新状态，明细项建议通过专门接口维护
        saleOrder.status = OrderStatus.valueOf(orderTO.status)
        return saleOrderRepository.save(saleOrder).toSaleOrderTO()
    }

    override fun deleteSaleOrder(orderTO: SaleOrderTO): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(orderTO.id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }
        saleOrderRepository.delete(saleOrder)
        return orderTO
    }

    override fun getSaleOrder(id: Long): SaleOrderTO {
        return saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }
            .toSaleOrderTO()
    }

    override fun listSaleOrdersByCustomer(customerId: Long): List<SaleOrderTO> {
        return saleOrderRepository.findByCustomerId(customerId).map { it.toSaleOrderTO() }
    }

    override fun listSaleOrders(): List<SaleOrderTO> {
        return saleOrderRepository.findAll().map { it.toSaleOrderTO() }
    }

    override fun completeOrder(id: Long): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }
        saleOrder.completeOrder()
        return saleOrderRepository.save(saleOrder).toSaleOrderTO()
    }

    private fun SaleOrder.toSaleOrderTO() = SaleOrderTO(
        id = id,
        customerId = customerId,
        status = status.name,
        items = items.map { it.toSaleItemTO() },
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun SaleItem.toSaleItemTO() = SaleItemTO(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice
    )
} 
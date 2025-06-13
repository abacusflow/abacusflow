package org.bruwave.abacusflow.usecase.transaction.impl

import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.transaction.SaleOrderItem
import org.bruwave.abacusflow.transaction.SaleOrder
import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderItemTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderService
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.UpdateSaleOrderInputTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SaleOrderServiceImpl(
    private val saleOrderRepository: SaleOrderRepository,
) : SaleOrderService {
    override fun createSaleOrder(input: CreateSaleOrderInputTO): SaleOrderTO {
        val saleOrder = SaleOrder(
            customerId = input.customerId,
        )
        input.orderItems.forEach {
            saleOrder.addItem(it.productId, it.quantity, it.unitPrice)
        }
        return saleOrderRepository.save(saleOrder).toTO()
    }

    override fun updateSaleOrder(id: Long, input: UpdateSaleOrderInputTO): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }

        saleOrder.apply {
            input.customerId?.let {
                changeCustomer(it)
            }

            clearItems()

            input.orderItems?.forEach {
                saleOrder.addItem(it.productId, it.quantity, it.unitPrice)
            }
        }

        return saleOrderRepository.save(saleOrder).toTO()
    }

    override fun deleteSaleOrder(id: Long): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }
        saleOrderRepository.delete(saleOrder)
        return saleOrder.toTO()
    }

    override fun getSaleOrder(id: Long): SaleOrderTO {
        return saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }
            .toTO()
    }

    override fun listSaleOrdersByCustomer(customerId: Long): List<BasicSaleOrderTO> {
        return saleOrderRepository.findByCustomerId(customerId).map { it.toBasicTO() }
    }

    override fun listSaleOrders(): List<BasicSaleOrderTO> {
        return saleOrderRepository.findAll().map { it.toBasicTO() }
    }

    override fun completeOrder(id: Long): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }
        saleOrder.completeOrder()
        return saleOrderRepository.save(saleOrder).toTO()
    }

    private fun SaleOrder.toTO() = SaleOrderTO(
        id = id,
        customerId = customerId,
        status = status.name,
        items = items.map { it.toTO() },
        createdAt = createdAt,
        updatedAt = updatedAt,
        orderNo = orderNo,
        orderDate = orderDate
    )

    private fun SaleOrder.toBasicTO() = BasicSaleOrderTO(
        id = id,
        status = status.name,
        itemCount = items.count(),
        createdAt = createdAt,
        orderNo = orderNo,
        customerName = "null",//TODO-NULL
        totalAmount = totalAmount,
        totalQuantity = totalQuantity,
        orderDate = orderDate,
    )

    private fun SaleOrderItem.toTO() = SaleOrderItemTO(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice,
        subtotal = subtotal
    )
} 
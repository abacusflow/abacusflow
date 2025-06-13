package org.bruwave.abacusflow.usecase.sale.impl

import org.bruwave.abacusflow.db.sale.SaleOrderRepository
import org.bruwave.abacusflow.sale.SaleOrder
import org.bruwave.abacusflow.usecase.sale.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SaleOrderServiceImpl(
    private val saleOrderRepository: SaleOrderRepository
) : SaleOrderService {

    override fun createSaleOrder(input: CreateSaleOrderInputTO): SaleOrderTO {
        val saleOrder = SaleOrder(
            customerId = input.customerId,
            orderNumber = input.orderNumber,
            orderDate = input.orderDate,
            status = input.status,
            totalAmount = input.totalAmount,
            paymentStatus = input.paymentStatus,
            shippingStatus = input.shippingStatus,
            notes = input.notes
        )
        return saleOrderRepository.save(saleOrder).toSaleOrderTO()
    }

    override fun updateSaleOrder(id: Long, input: UpdateSaleOrderInputTO): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Sale order not found with id: $id") }
        
        saleOrder.apply {
            status = input.status
            totalAmount = input.totalAmount
            paymentStatus = input.paymentStatus
            shippingStatus = input.shippingStatus
            notes = input.notes
        }
        
        return saleOrderRepository.save(saleOrder).toSaleOrderTO()
    }

    override fun deleteSaleOrder(id: Long): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Sale order not found with id: $id") }
        
        saleOrderRepository.delete(saleOrder)
        return saleOrder.toSaleOrderTO()
    }

    override fun getSaleOrder(id: Long): SaleOrderTO {
        return saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Sale order not found with id: $id") }
            .toSaleOrderTO()
    }

    override fun listSaleOrders(): List<BasicSaleOrderTO> {
        return saleOrderRepository.findAll().map { it.toBasicSaleOrderTO() }
    }

    override fun listSaleOrdersByCustomer(customerId: Long): List<BasicSaleOrderTO> {
        return saleOrderRepository.findByCustomerId(customerId).map { it.toBasicSaleOrderTO() }
    }

    override fun updateOrderStatus(id: Long, status: String): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Sale order not found with id: $id") }
        
        saleOrder.status = status
        return saleOrderRepository.save(saleOrder).toSaleOrderTO()
    }

    override fun updatePaymentStatus(id: Long, status: String): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Sale order not found with id: $id") }
        
        saleOrder.paymentStatus = status
        return saleOrderRepository.save(saleOrder).toSaleOrderTO()
    }

    override fun updateShippingStatus(id: Long, status: String): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("Sale order not found with id: $id") }
        
        saleOrder.shippingStatus = status
        return saleOrderRepository.save(saleOrder).toSaleOrderTO()
    }
}

private fun SaleOrder.toSaleOrderTO() = SaleOrderTO(
    id = id,
    customerId = customerId,
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

private fun SaleOrder.toBasicSaleOrderTO() = BasicSaleOrderTO(
    id = id,
    customerId = customerId,
    orderNumber = orderNumber,
    orderDate = orderDate,
    status = status,
    totalAmount = totalAmount,
    paymentStatus = paymentStatus,
    shippingStatus = shippingStatus
) 
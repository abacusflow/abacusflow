package org.bruwave.abacusflow.usecase.transaction.impl

import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.transaction.SaleItem
import org.bruwave.abacusflow.transaction.SaleOrder
import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.SaleItemTO
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
        input.items.forEach {
            saleOrder.addItem(it.productId, it.quantity, it.unitPrice)
        }
        return saleOrderRepository.save(saleOrder).toTO()
    }

    override fun updateSaleOrder(id: Long, input: UpdateSaleOrderInputTO): SaleOrderTO {
        val saleOrder = saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }

        saleOrder.apply {
            changeCustomer(input.customerId)
            items.clear()
            input.items.forEach {
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
        updatedAt = updatedAt
    )

    private fun SaleOrder.toBasicTO() = BasicSaleOrderTO(
        id = id,
        customerId = customerId,
        status = status.name,
        itemCount = items.count(),
        createdAt = createdAt,
    )

    private fun SaleItem.toTO() = SaleItemTO(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice
    )
} 
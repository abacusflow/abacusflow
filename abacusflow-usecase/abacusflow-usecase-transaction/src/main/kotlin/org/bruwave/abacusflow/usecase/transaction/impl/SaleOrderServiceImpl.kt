package org.bruwave.abacusflow.usecase.transaction.impl

import org.bruwave.abacusflow.db.partner.CustomerRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.transaction.SaleOrder
import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderService
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.UpdateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SaleOrderServiceImpl(
    private val saleOrderRepository: SaleOrderRepository,
    private val customerRepository: CustomerRepository,
) : SaleOrderService {
    override fun createSaleOrder(input: CreateSaleOrderInputTO): SaleOrderTO {
        val saleOrder = SaleOrder(
            customerId = input.customerId,
            orderDate = input.orderDate,
            note = input.note,
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

            input.orderDate?.let {
                changeOrderDate(it)
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
        val customer = customerRepository.findById(customerId)
            .orElseThrow { NoSuchElementException("Supplier not found") }

        return saleOrderRepository.findAll().map { it.toBasicTO(customer.name) }
    }

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
        val saleOrder = saleOrderRepository.findById(id)
            .orElseThrow { NoSuchElementException("SaleOrder not found") }
        saleOrder.completeOrder()
        return saleOrderRepository.save(saleOrder).toTO()
    }
}
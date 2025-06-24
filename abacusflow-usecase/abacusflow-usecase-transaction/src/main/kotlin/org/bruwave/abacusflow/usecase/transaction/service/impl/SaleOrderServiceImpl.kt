package org.bruwave.abacusflow.usecase.transaction.service.impl

import org.bruwave.abacusflow.db.inventory.InventoryUnitRepository
import org.bruwave.abacusflow.db.partner.CustomerRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.inventory.InventoryUnit
import org.bruwave.abacusflow.transaction.SaleOrder
import org.bruwave.abacusflow.transaction.SaleOrderItem
import org.bruwave.abacusflow.transaction.TransactionInventoryUnitType
import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.SaleItemInputTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.transaction.mapper.toTO
import org.bruwave.abacusflow.usecase.transaction.service.SaleOrderService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class SaleOrderServiceImpl(
    private val saleOrderRepository: SaleOrderRepository,
    private val customerRepository: CustomerRepository,
    private val inventoryUnitRepository: InventoryUnitRepository,
) : SaleOrderService {
    override fun createSaleOrder(input: CreateSaleOrderInputTO): SaleOrderTO {
        val inventoryUnits =
            inventoryUnitRepository.findAllById(
                input.orderItems
                    .map { it.inventoryUnitId }
                    .distinct(),
            )
        val inventoryUnitMapById = inventoryUnits.associateBy { it.id }

        val orderItems =
            input.orderItems.map { item ->
                mapInputOrderItemToOrderItem(item, inventoryUnitMapById)
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
        inventoryUnitMapById: Map<Long, InventoryUnit>,
    ): SaleOrderItem {
        val inventoryUnit = inventoryUnitMapById.getValue(item.inventoryUnitId)

        return when (inventoryUnit) {
            is InventoryUnit.BatchInventoryUnit ->
                SaleOrderItem(
                    item.inventoryUnitId,
                    TransactionInventoryUnitType.BATCH,
                    item.quantity,
                    item.unitPrice,
                    item.discountFactor ?: BigDecimal.ONE,
                )

            is InventoryUnit.InstanceInventoryUnit -> {
                SaleOrderItem(
                    item.inventoryUnitId,
                    TransactionInventoryUnitType.INSTANCE,
                    1, // 资产类固定数量为1
                    item.unitPrice,
                    item.discountFactor ?: BigDecimal.ONE
                )
            }

            else -> throw IllegalArgumentException("Unsupported inventory unit item type ${inventoryUnit::class.java}")
        }
    }
}

package org.abacusflow.usecase.transaction.service.impl

import org.abacusflow.db.inventory.InventoryUnitRepository
import org.abacusflow.db.partner.CustomerRepository
import org.abacusflow.db.transaction.SaleOrderRepository
import org.abacusflow.inventory.InventoryUnit
import org.abacusflow.transaction.SaleOrder
import org.abacusflow.transaction.SaleOrderItem
import org.abacusflow.transaction.TransactionInventoryUnitType
import org.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.abacusflow.usecase.transaction.SaleItemInputTO
import org.abacusflow.usecase.transaction.SaleOrderTO
import org.abacusflow.usecase.transaction.mapper.toTO
import org.abacusflow.usecase.transaction.service.SaleOrderCommandService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
class SaleOrderCommandServiceImpl(
    private val saleOrderRepository: SaleOrderRepository,
    private val customerRepository: CustomerRepository,
    private val inventoryUnitRepository: InventoryUnitRepository,
) : SaleOrderCommandService {
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
                    item.discountFactor ?: BigDecimal.ONE,
                )
            }

            else -> throw IllegalArgumentException("Unsupported inventory unit item type ${inventoryUnit::class.java}")
        }
    }
}

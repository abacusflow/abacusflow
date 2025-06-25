package org.bruwave.abacusflow.usecase.inventory.listener

import org.bruwave.abacusflow.db.inventory.InventoryUnitRepository
import org.bruwave.abacusflow.inventory.InventoryUnit
import org.bruwave.abacusflow.transaction.SaleOrderCompletedEvent
import org.bruwave.abacusflow.transaction.SaleOrderReversedEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class InventorySaleOrderEventListener(
    private val inventoryUnitRepository: InventoryUnitRepository,
) {
    @EventListener
    fun handleSaleOrderCompletedEvent(event: SaleOrderCompletedEvent) {
        val order = event.order
        println("SaleOrder Completed orderNo: ${order.no}")

        order.items.groupBy { it.inventoryUnitId }.forEach { (inventoryUnitId, inventories) ->
            val totalQuantity = inventories.sumOf { it.quantity }

            val units =
                inventoryUnitRepository.findByIdAndStatus(
                    inventoryUnitId,
                    InventoryUnit.InventoryUnitStatus.NORMAL,
                )

            units.forEach { unit ->
                unit.consume(order.id, totalQuantity)
            }
        }
    }

    @EventListener
    fun handleSaleOrderReversedEvent(event: SaleOrderReversedEvent) {
        val order = event.order
        println("SaleOrder Reversed orderNo: ${order.no}")

        // 查询该订单产生的库存单元
        val units = inventoryUnitRepository.findAllBySaleOrderId(order.id)

        if (units.isEmpty()) {
            println("No InventoryUnits found for PurchaseOrder ${order.no}, skipping reversal")
            return
        }

        val sumItemQuantityByUnitId: Map<Long, Int> =
            order.items
                .groupBy { it.inventoryUnitId }
                .mapValues { (_, items) -> items.sumOf { it.quantity } }

        units.forEach { unit ->
            val quantityForReverse = sumItemQuantityByUnitId.getValue(unit.id)

            unit.reverse(quantityForReverse, order.id)
        }

        inventoryUnitRepository.saveAll(units)
    }
}

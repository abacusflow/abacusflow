package org.bruwave.abacusflow.usecase.inventory.listener

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.db.inventory.InventoryUnitRepository
import org.bruwave.abacusflow.inventory.InventoryUnit
import org.bruwave.abacusflow.transaction.PurchaseOrderCompletedEvent
import org.bruwave.abacusflow.transaction.PurchaseOrderReversedEvent
import org.bruwave.abacusflow.transaction.TransactionProductType.ASSET
import org.bruwave.abacusflow.transaction.TransactionProductType.MATERIAL
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class InventoryPurchaseOrderEventListener(
    private val inventoryRepository: InventoryRepository,
    private val inventoryUnitRepository: InventoryUnitRepository,
) {
    @EventListener
    fun handlePurchaseOrderCompletedEvent(event: PurchaseOrderCompletedEvent) {
        val order = event.order
        println("PurchaseOrder Completed orderNo: ${order.no}")

        order.items.groupBy { it.productId }.forEach { (productId, products) ->
            val inventory =
                inventoryRepository.findByProductId(productId)
                    ?: throw NoSuchElementException("Product with id $productId not found")

            val units =
                products.groupBy { it.productType }.flatMap { (type, productsByType) ->
                    val simpleProduct = productsByType.first()
                    val sumQuantity = productsByType.sumOf { it.quantity.toLong() }

                    when (type) {
                        MATERIAL -> {
                            val unitPriceSet = productsByType.map { it.unitPrice }.toSet()
                            require(unitPriceSet.size == 1) { "MATERIAL 产品存在多个不同单价" }

                            listOf(
                                InventoryUnit.BatchInventoryUnit(
                                    inventory,
                                    purchaseOrderId = order.id,
                                    quantity = sumQuantity,
                                    unitPrice = simpleProduct.unitPrice,
                                    depotId = null,
                                ),
                            )
                        }

                        ASSET ->
                            productsByType.map { item ->
                                require(item.quantity == 1) { "资产类产品每项 quantity 应为 1" }
                                val serialNumber = requireNotNull(item.serialNumber) { "资产类产品缺少 serialNumber" }

                                InventoryUnit.InstanceInventoryUnit(
                                    inventory,
                                    purchaseOrderId = order.id,
                                    unitPrice = item.unitPrice,
                                    serialNumber = serialNumber,
                                    depotId = null,
                                )
                            }
                    }
                }

            if (units.size > 0) {
                inventoryUnitRepository.saveAll(units)
            }
        }
    }

    @EventListener
    fun handlePurchaseOrderReversedEvent(event: PurchaseOrderReversedEvent) {
        val order = event.order
        println("PurchaseOrder Reversed orderNo: ${order.no}")

        // 查询该订单产生的库存单元
        val units = inventoryUnitRepository.findByPurchaseOrderId(order.id)

        if (units.isEmpty()) {
            println("No InventoryUnits found for PurchaseOrder ${order.no}, skipping reversal")
            return
        }

        // 只要有一个库存单元 saleOrderIds 不为空 或 状态不是 NORMAL，则禁止撤回
        val hasBeenConsumed =
            units.any {
                it.saleOrderIds.isNotEmpty() || it.status != InventoryUnit.InventoryUnitStatus.NORMAL
            }

        if (hasBeenConsumed) {
            println("Cannot reverse PurchaseOrder ${order.no}: at least one InventoryUnit has been consumed or associated with a SaleOrder")
            throw IllegalStateException("该采购单下存在已出库或已销售的库存，无法撤回")
        }

        inventoryUnitRepository.deleteAll(units)
    }
}

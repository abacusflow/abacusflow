package org.bruwave.abacusflow.usecase.product.listener

import org.bruwave.abacusflow.db.product.ProductInstanceRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.product.ProductInstance
import org.bruwave.abacusflow.transaction.PurchaseOrderItemChangedEvent
import org.bruwave.abacusflow.transaction.TransactionProductType
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ProductionPurchaseOrderEventListener(
    private val productRepository: ProductRepository,
    private val productInstanceRepository: ProductInstanceRepository,
    private val purchaseOrderRepository: PurchaseOrderRepository,
) {

    @EventListener
    fun handlePurchaseOrderCreated(event: PurchaseOrderItemChangedEvent) {
        println("PurchaseOrder Created orderNo: ${event.orderNo}")

        val products = productRepository.findAllById(
            event.items
                .map { it.productId }
                .distinct()
        )
        val productMapById = products.associateBy { it.id }

        val needCreateProductInstances = event.items.mapNotNull { item ->
            when (item.productType) {
                TransactionProductType.MATERIAL -> null
                TransactionProductType.ASSET -> {
                    val serialNumber = requireNotNull(item.serialNumber) { "" }
                    val product = productMapById[item.productId]
                        ?: throw RuntimeException("Product with id ${item.productId} not found")
                    // 为资产类产品创建产品实例

                    ProductInstance(
                        serialNumber = serialNumber,
                        product = product,
                        purchaseOrderId = event.orderId,
                        saleOrderId = null
                    )
                }
            }
        }
        if (needCreateProductInstances.size > 0) {
            val createdInstances = productInstanceRepository.saveAll(needCreateProductInstances)

            // 重新加载订单以获取可修改的实体
            val order = purchaseOrderRepository.findById(event.orderId)
                .orElseThrow { RuntimeException("PurchaseOrder with id ${event.orderId} not found") }
            // 更新订单项的 instanceId
            val instanceMap = createdInstances.associateBy { it.serialNumber }
            order.items.forEach { item ->
                if (item.productType == TransactionProductType.ASSET) {
                    val instance = instanceMap[item.serialNumber]
                        ?: throw RuntimeException("Product with id ${item.serialNumber} not found")
                    item.fillRealProductInstance(instance.id)
                }
            }

            // 保存更新后的订单
            purchaseOrderRepository.save(order)
        }
    }
}
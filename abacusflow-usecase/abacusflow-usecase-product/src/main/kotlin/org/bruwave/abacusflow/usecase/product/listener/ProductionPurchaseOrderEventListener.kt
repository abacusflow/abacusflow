package org.bruwave.abacusflow.usecase.product.listener

import org.bruwave.abacusflow.db.product.ProductInstanceRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.product.ProductInstance
import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.bruwave.abacusflow.transaction.PurchaseOrderCompletedEvent
import org.bruwave.abacusflow.transaction.TransactionProductType
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class ProductionPurchaseOrderEventListener(
    private val productRepository: ProductRepository,
    private val productInstanceRepository: ProductInstanceRepository,
) {
    @EventListener
    fun handlePurchaseOrderCompletedEvent(event: PurchaseOrderCompletedEvent) {
        println("PurchaseOrder Created orderNo: ${event.order.no}")
        val products =
            productRepository.findAllById(
                event.order.items
                    .map { it.productId }
                    .distinct(),
            )
        val productMapById = products.associateBy { it.id }

        val needCreateProductInstances =
            getNeedCreateProductInstancesByOrder(
                event.order,
                productMapById,
            )

        if (needCreateProductInstances.size > 0) {
            productInstanceRepository.saveAll(needCreateProductInstances)
        }
    }

    private fun getNeedCreateProductInstancesByOrder(
        order: PurchaseOrder,
        productMapById: Map<Long, Product>,
    ): List<ProductInstance> {
        return order.items.mapNotNull { item ->
            when (item.productType) {
                TransactionProductType.MATERIAL -> null
                TransactionProductType.ASSET -> {
                    val serialNumber = requireNotNull(item.serialNumber) { "" }
                    val product =
                        productMapById[item.productId]
                            ?: throw RuntimeException("Product with id ${item.productId} not found")

                    ProductInstance(
                        serialNumber = serialNumber,
                        product = product,
                        purchaseOrderId = order.id,
                        saleOrderId = null,
                    )
                }
            }
        }
    }
}

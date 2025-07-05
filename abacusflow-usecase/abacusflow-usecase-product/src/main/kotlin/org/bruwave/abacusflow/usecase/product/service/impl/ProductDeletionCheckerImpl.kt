package org.bruwave.abacusflow.usecase.product.service.impl

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.db.inventory.InventoryUnitRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderItemRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderItemRepository
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.product.service.ProductDeletionChecker
import org.springframework.stereotype.Service

@Service
class ProductDeletionCheckerImpl(
    private val inventoryRepository: InventoryRepository,
    private val inventoryUnitRepository: InventoryUnitRepository,
    private val saleOrderItemRepository: SaleOrderItemRepository,
    private val purchaseOrderItemRepository: PurchaseOrderItemRepository,
) : ProductDeletionChecker {
    override fun canDelete(product: Product): Boolean {
        val productId = product.id

        // 查询所有与该产品相关的库存单位
        val relatedInventoryUnits = inventoryUnitRepository.findByInventoryProductId(productId)
        val relatedInventoryUnitIds = relatedInventoryUnits.map { it.id }

        val noPurchaseOrder = purchaseOrderItemRepository.countPurchaseOrderItemByProductId(productId) == 0L
        val noSaleOrder =
            if (relatedInventoryUnitIds.isEmpty()) {
                true // 没有库存单位就说明没有入库更是没有销售绑定
            } else {
                saleOrderItemRepository.countSaleOrderItemByInventoryUnitIdIn(relatedInventoryUnitIds) == 0L
            }

        return noPurchaseOrder && noSaleOrder
    }
}

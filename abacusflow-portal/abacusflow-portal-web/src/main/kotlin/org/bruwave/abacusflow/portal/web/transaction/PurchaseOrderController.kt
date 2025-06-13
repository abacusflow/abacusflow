package org.bruwave.abacusflow.portal.web.purchase

import org.bruwave.abacusflow.portal.web.api.PurchaseOrdersApi
import org.bruwave.abacusflow.portal.web.model.BasicPurchaseOrderVO
import org.bruwave.abacusflow.portal.web.model.CreatePurchaseOrderInputVO
import org.bruwave.abacusflow.portal.web.model.PurchaseOrderVO
import org.bruwave.abacusflow.portal.web.model.UpdatePurchaseOrderInputVO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PurchaseOrderController(
    private val purchaseOrderService: PurchaseOrderService
): PurchaseOrdersApi {

    override fun listPurchaseOrders(): ResponseEntity<List<BasicPurchaseOrderVO>> {
        val orders = purchaseOrderService.listPurchaseOrders()
        val orderVOs = orders.map { order ->
            BasicPurchaseOrderVO(
                order.id,
                order.supplierId,
                order.items
            )
        }
        return ResponseEntity.ok(orderVOs)
    }

    override fun getPurchaseOrder(id: Long): ResponseEntity<PurchaseOrderVO> {
        val order = purchaseOrderService.getPurchaseOrder(id)
        return ResponseEntity.ok(
            PurchaseOrderVO(
                order.id,
                order.supplierId,
                order.items,
                order.createdAt.toEpochMilli(),
                order.updatedAt.toEpochMilli()
            )
        )
    }

    override fun addPurchaseOrder(createPurchaseOrderInputVO: CreatePurchaseOrderInputVO): ResponseEntity<PurchaseOrderVO> {
        val order = purchaseOrderService.createPurchaseOrder(
            CreatePurchaseOrderInputTO(
                createPurchaseOrderInputVO.supplierId,
                createPurchaseOrderInputVO.items
            )
        )
        return ResponseEntity.ok(
            PurchaseOrderVO(
                order.id,
                order.supplierId,
                order.items,
                order.createdAt.toEpochMilli(),
                order.updatedAt.toEpochMilli()
            )
        )
    }

    override fun updatePurchaseOrder(
        id: Long,
        updatePurchaseOrderInputVO: UpdatePurchaseOrderInputVO
    ): ResponseEntity<PurchaseOrderVO> {
        val order = purchaseOrderService.updatePurchaseOrder(
            id,
            UpdatePurchaseOrderInputTO(
                supplierId = updatePurchaseOrderInputVO.supplierId,
                items = updatePurchaseOrderInputVO.items
            )
        )
        return ResponseEntity.ok(
            PurchaseOrderVO(
                order.id,
                order.supplierId,
                order.items,
                order.createdAt.toEpochMilli(),
                order.updatedAt.toEpochMilli()
            )
        )
    }

    override fun deletePurchaseOrder(id: Long): ResponseEntity<PurchaseOrderVO> {
        purchaseOrderService.deletePurchaseOrder(id)
        return ResponseEntity.ok().build()
    }
}
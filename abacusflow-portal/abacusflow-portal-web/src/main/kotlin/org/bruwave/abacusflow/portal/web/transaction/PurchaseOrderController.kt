package org.bruwave.abacusflow.portal.web.transaction

import org.bruwave.abacusflow.portal.web.api.PurchaseOrdersApi
import org.bruwave.abacusflow.portal.web.model.BasicPurchaseOrderVO
import org.bruwave.abacusflow.portal.web.model.CreatePurchaseOrderInputVO
import org.bruwave.abacusflow.portal.web.model.PurchaseOrderVO
import org.bruwave.abacusflow.usecase.transaction.CreatePurchaseOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.service.PurchaseOrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PurchaseOrderController(
    private val purchaseOrderService: PurchaseOrderService,
) : PurchaseOrdersApi {
    override fun listPurchaseOrders(): ResponseEntity<List<BasicPurchaseOrderVO>> {
        val orderVOs =
            purchaseOrderService.listPurchaseOrders().map { order ->
                order.toBasicVO()
            }
        return ResponseEntity.ok(orderVOs)
    }

    override fun getPurchaseOrder(id: Long): ResponseEntity<PurchaseOrderVO> {
        val order = purchaseOrderService.getPurchaseOrder(id)
        return ResponseEntity.ok(
            order.toVO(),
        )
    }

    override fun addPurchaseOrder(createPurchaseOrderInputVO: CreatePurchaseOrderInputVO): ResponseEntity<PurchaseOrderVO> {
        val order =
            purchaseOrderService.createPurchaseOrder(
                CreatePurchaseOrderInputTO(
                    supplierId = createPurchaseOrderInputVO.supplierId,
                    orderDate = createPurchaseOrderInputVO.orderDate,
                    orderItems = createPurchaseOrderInputVO.orderItems.map { it.toInputTO() },
                    note = createPurchaseOrderInputVO.note,
                ),
            )
        return ResponseEntity.ok(
            order.toVO(),
        )
    }

    override fun completePurchaseOrder(id: Long): ResponseEntity<Unit> {
        purchaseOrderService.completeOrder(id)
        return ResponseEntity.ok().build()
    }

    override fun cancelPurchaseOrder(id: Long): ResponseEntity<Unit> {
        purchaseOrderService.cancelOrder(id)
        return ResponseEntity.ok().build()
    }

    override fun reversePurchaseOrder(id: Long): ResponseEntity<Unit> {
        purchaseOrderService.reverseOrder(id)
        return ResponseEntity.ok().build()
    }
}

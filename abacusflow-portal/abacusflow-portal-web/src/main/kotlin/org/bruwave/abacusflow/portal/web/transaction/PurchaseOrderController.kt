package org.bruwave.abacusflow.portal.web.transaction

import org.bruwave.abacusflow.portal.web.api.PurchaseOrdersApi
import org.bruwave.abacusflow.portal.web.model.BasicPurchaseOrderVO
import org.bruwave.abacusflow.portal.web.model.CreatePurchaseOrderInputVO
import org.bruwave.abacusflow.portal.web.model.PurchaseOrderVO
import org.bruwave.abacusflow.portal.web.model.UpdatePurchaseOrderInputVO
import org.bruwave.abacusflow.usecase.transaction.CreatePurchaseOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderService
import org.bruwave.abacusflow.usecase.transaction.UpdatePurchaseOrderInputTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class PurchaseOrderController(
    private val purchaseOrderService: PurchaseOrderService
) : PurchaseOrdersApi {

    override fun listPurchaseOrders(): ResponseEntity<List<BasicPurchaseOrderVO>> {
        val orderVOs = purchaseOrderService.listPurchaseOrders().map { order ->
            order.toBasicVO()
        }
        return ResponseEntity.ok(orderVOs)
    }

    override fun getPurchaseOrder(id: Long): ResponseEntity<PurchaseOrderVO> {
        val order = purchaseOrderService.getPurchaseOrder(id)
        return ResponseEntity.ok(
            order.toVO()
        )
    }

    override fun addPurchaseOrder(createPurchaseOrderInputVO: CreatePurchaseOrderInputVO): ResponseEntity<PurchaseOrderVO> {
        val order = purchaseOrderService.createPurchaseOrder(
            CreatePurchaseOrderInputTO(
                supplierId = createPurchaseOrderInputVO.supplierId,
                orderDate = createPurchaseOrderInputVO.orderDate,
                orderItems = createPurchaseOrderInputVO.orderItems.map { it.toInputTO() },
                note = createPurchaseOrderInputVO.note
            )
        )
        return ResponseEntity.ok(
            order.toVO()
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
                orderDate = updatePurchaseOrderInputVO.orderDate,
                orderItems = updatePurchaseOrderInputVO.orderItems?.map { it.toInputTO() },
                note = updatePurchaseOrderInputVO.note
            )
        )
        return ResponseEntity.ok(
            order.toVO()
        )
    }

    override fun deletePurchaseOrder(id: Long): ResponseEntity<PurchaseOrderVO> {
        purchaseOrderService.deletePurchaseOrder(id)
        return ResponseEntity.ok().build()
    }
}
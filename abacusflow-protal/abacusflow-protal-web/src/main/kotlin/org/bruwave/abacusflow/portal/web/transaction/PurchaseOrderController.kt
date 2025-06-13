package org.bruwave.abacusflow.portal.web.transaction

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
        return super.listPurchaseOrders()
    }

    override fun getPurchaseOrder(id: Long): ResponseEntity<PurchaseOrderVO> {
        return super.getPurchaseOrder(id)
    }

    override fun addPurchaseOrder(createPurchaseOrderInputVO: CreatePurchaseOrderInputVO): ResponseEntity<PurchaseOrderVO> {
        return super.addPurchaseOrder(createPurchaseOrderInputVO)
    }

    override fun updatePurchaseOrder(
        id: Long,
        updatePurchaseOrderInputVO: UpdatePurchaseOrderInputVO
    ): ResponseEntity<PurchaseOrderVO> {
        return super.updatePurchaseOrder(id, updatePurchaseOrderInputVO)
    }

    override fun deletePurchaseOrder(id: Long): ResponseEntity<PurchaseOrderVO> {
        return super.deletePurchaseOrder(id)
    }
}
package org.bruwave.abacusflow.portal.web.transaction

import org.apache.tomcat.jni.Buffer.address
import org.bruwave.abacusflow.portal.web.api.PurchaseOrdersApi
import org.bruwave.abacusflow.portal.web.model.BasicPurchaseOrderVO
import org.bruwave.abacusflow.portal.web.model.CreatePurchaseOrderInputVO
import org.bruwave.abacusflow.portal.web.model.ListPurchaseOrdersPage200ResponseVO
import org.bruwave.abacusflow.portal.web.model.ListSuppliersPage200ResponseVO
import org.bruwave.abacusflow.portal.web.model.PurchaseOrderVO
import org.bruwave.abacusflow.portal.web.partner.toBasicVO
import org.bruwave.abacusflow.usecase.transaction.CreatePurchaseOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.service.PurchaseOrderCommandService
import org.bruwave.abacusflow.usecase.transaction.service.PurchaseOrderQueryService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class PurchaseOrderController(
    private val purchaseOrderCommandService: PurchaseOrderCommandService,
    private val purchaseOrderQueryService: PurchaseOrderQueryService,
) : PurchaseOrdersApi {
    override fun listPurchaseOrdersPage(
        pageIndex: Int,
        pageSize: Int,
        orderNo: UUID?,
        supplierName: String?,
        status: String?,
        productName: String?
    ): ResponseEntity<ListPurchaseOrdersPage200ResponseVO> {
        val pageable = PageRequest.of(pageIndex - 1, pageSize)

        val page = purchaseOrderQueryService.listPurchaseOrdersPage(
            pageable,
            orderNo = orderNo,
            supplierName = supplierName,
            status = status,
            productName = productName,
        ).map { it.toBasicVO() }

        val pageVO = ListPurchaseOrdersPage200ResponseVO(
            content = page.content,
            totalElements = page.totalElements,
            number = page.number,
            propertySize = page.size
        )

        return ResponseEntity.ok(pageVO)
    }

    override fun getPurchaseOrder(id: Long): ResponseEntity<PurchaseOrderVO> {
        val order = purchaseOrderCommandService.getPurchaseOrder(id)
        return ResponseEntity.ok(
            order.toVO(),
        )
    }

    override fun addPurchaseOrder(createPurchaseOrderInputVO: CreatePurchaseOrderInputVO): ResponseEntity<PurchaseOrderVO> {
        val order =
            purchaseOrderCommandService.createPurchaseOrder(
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
        purchaseOrderCommandService.completeOrder(id)
        return ResponseEntity.ok().build()
    }

    override fun cancelPurchaseOrder(id: Long): ResponseEntity<Unit> {
        purchaseOrderCommandService.cancelOrder(id)
        return ResponseEntity.ok().build()
    }

    override fun reversePurchaseOrder(id: Long): ResponseEntity<Unit> {
        purchaseOrderCommandService.reverseOrder(id)
        return ResponseEntity.ok().build()
    }
}

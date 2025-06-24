package org.bruwave.abacusflow.portal.web.transaction

import org.apache.tomcat.jni.Buffer.address
import org.bruwave.abacusflow.portal.web.api.SaleOrdersApi
import org.bruwave.abacusflow.portal.web.model.BasicSaleOrderVO
import org.bruwave.abacusflow.portal.web.model.CreateSaleOrderInputVO
import org.bruwave.abacusflow.portal.web.model.ListSaleOrdersPage200ResponseVO
import org.bruwave.abacusflow.portal.web.model.ListSuppliersPage200ResponseVO
import org.bruwave.abacusflow.portal.web.model.SaleOrderVO
import org.bruwave.abacusflow.portal.web.partner.toBasicVO
import org.bruwave.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.service.SaleOrderCommandService
import org.bruwave.abacusflow.usecase.transaction.service.SaleOrderQueryService
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class SaleOrderController(
    private val saleOrderCommandService: SaleOrderCommandService,
    private val saleOrderQueryService: SaleOrderQueryService,
) : SaleOrdersApi {
    override fun listSaleOrdersPage(
        pageIndex: Int,
        pageSize: Int,
        orderNo: UUID?,
        customerName: String?,
        status: String?,
        productName: String?
    ): ResponseEntity<ListSaleOrdersPage200ResponseVO> {
        val pageable = PageRequest.of(pageIndex - 1, pageSize)

        val page = saleOrderQueryService.listSaleOrdersPage(
            pageable,
            orderNo = orderNo,
            customerName = customerName,
            status = status,
            productName = productName,
        ).map { it.toBasicVO() }

        val pageVO = ListSaleOrdersPage200ResponseVO(
            content = page.content,
            totalElements = page.totalElements,
            number = page.number,
            propertySize = page.size
        )

        return ResponseEntity.ok(pageVO)
    }

    override fun getSaleOrder(id: Long): ResponseEntity<SaleOrderVO> {
        val order = saleOrderCommandService.getSaleOrder(id)
        return ResponseEntity.ok(
            order.toVO(),
        )
    }

    override fun addSaleOrder(createSaleOrderInputVO: CreateSaleOrderInputVO): ResponseEntity<SaleOrderVO> {
        val order =
            saleOrderCommandService.createSaleOrder(
                CreateSaleOrderInputTO(
                    createSaleOrderInputVO.customerId,
                    createSaleOrderInputVO.orderDate,
                    createSaleOrderInputVO.orderItems.map { it.toInputTO() },
                    createSaleOrderInputVO.note,
                ),
            )
        return ResponseEntity.ok(
            order.toVO(),
        )
    }

    override fun completeSaleOrder(id: Long): ResponseEntity<Unit> {
        saleOrderCommandService.completeOrder(id)
        return ResponseEntity.ok().build()
    }

    override fun cancelSaleOrder(id: Long): ResponseEntity<Unit> {
        saleOrderCommandService.cancelOrder(id)
        return ResponseEntity.ok().build()
    }

    override fun reverseSaleOrder(id: Long): ResponseEntity<Unit> {
        saleOrderCommandService.reverseOrder(id)
        return ResponseEntity.ok().build()
    }
}

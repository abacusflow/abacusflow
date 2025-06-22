package org.bruwave.abacusflow.portal.web.transaction

import org.bruwave.abacusflow.portal.web.api.SaleOrdersApi
import org.bruwave.abacusflow.portal.web.model.BasicSaleOrderVO
import org.bruwave.abacusflow.portal.web.model.CreateSaleOrderInputVO
import org.bruwave.abacusflow.portal.web.model.SaleOrderVO
import org.bruwave.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.service.SaleOrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SaleOrderController(
    private val saleOrderService: SaleOrderService,
) : SaleOrdersApi {
    override fun listSaleOrders(): ResponseEntity<List<BasicSaleOrderVO>> {
        val orders = saleOrderService.listSaleOrders()
        val orderVOs =
            orders.map { order ->
                order.toBasicVO()
            }
        return ResponseEntity.ok(orderVOs)
    }

    override fun getSaleOrder(id: Long): ResponseEntity<SaleOrderVO> {
        val order = saleOrderService.getSaleOrder(id)
        return ResponseEntity.ok(
            order.toVO(),
        )
    }

    override fun addSaleOrder(createSaleOrderInputVO: CreateSaleOrderInputVO): ResponseEntity<SaleOrderVO> {
        val order =
            saleOrderService.createSaleOrder(
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
        saleOrderService.completeOrder(id)
        return ResponseEntity.ok().build()
    }

    override fun cancelSaleOrder(id: Long): ResponseEntity<Unit> {
        saleOrderService.cancelOrder(id)
        return ResponseEntity.ok().build()
    }

    override fun reverseSaleOrder(id: Long): ResponseEntity<Unit> {
        saleOrderService.reverseOrder(id)
        return ResponseEntity.ok().build()
    }
}

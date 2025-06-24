package org.bruwave.abacusflow.portal.web.transaction

import org.bruwave.abacusflow.portal.web.api.SaleOrdersApi
import org.bruwave.abacusflow.portal.web.model.BasicSaleOrderVO
import org.bruwave.abacusflow.portal.web.model.CreateSaleOrderInputVO
import org.bruwave.abacusflow.portal.web.model.SaleOrderVO
import org.bruwave.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.service.SaleOrderCommandService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SaleOrderController(
    private val saleOrderCommandService: SaleOrderCommandService,
) : SaleOrdersApi {
    override fun listSaleOrders(): ResponseEntity<List<BasicSaleOrderVO>> {
        val orders = saleOrderCommandService.listSaleOrders()
        val orderVOs =
            orders.map { order ->
                order.toBasicVO()
            }
        return ResponseEntity.ok(orderVOs)
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

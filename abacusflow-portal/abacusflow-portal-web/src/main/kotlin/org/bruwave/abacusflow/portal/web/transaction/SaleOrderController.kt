package org.bruwave.abacusflow.portal.web.sale

import org.bruwave.abacusflow.portal.web.api.SaleOrdersApi
import org.bruwave.abacusflow.portal.web.model.BasicSaleOrderVO
import org.bruwave.abacusflow.portal.web.model.CreateSaleOrderInputVO
import org.bruwave.abacusflow.portal.web.model.SaleOrderVO
import org.bruwave.abacusflow.portal.web.model.UpdateSaleOrderInputVO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class SaleOrderController(
    private val saleOrderService: SaleOrderService
): SaleOrdersApi {

    override fun listSaleOrders(): ResponseEntity<List<BasicSaleOrderVO>> {
        val orders = saleOrderService.listSaleOrders()
        val orderVOs = orders.map { order ->
            BasicSaleOrderVO(
                order.id,
                order.customerId,
                order.orderDate,
                order.status
            )
        }
        return ResponseEntity.ok(orderVOs)
    }

    override fun getSaleOrder(id: Long): ResponseEntity<SaleOrderVO> {
        val order = saleOrderService.getSaleOrder(id)
        return ResponseEntity.ok(
            SaleOrderVO(
                order.id,
                order.customerId,
                order.orderDate,
                order.status,
                order.createdAt.toEpochMilli(),
                order.updatedAt.toEpochMilli()
            )
        )
    }

    override fun addSaleOrder(createSaleOrderInputVO: CreateSaleOrderInputVO): ResponseEntity<SaleOrderVO> {
        val order = saleOrderService.createSaleOrder(
            CreateSaleOrderInputTO(
                createSaleOrderInputVO.customerId,
                createSaleOrderInputVO.orderDate,
                createSaleOrderInputVO.status
            )
        )
        return ResponseEntity.ok(
            SaleOrderVO(
                order.id,
                order.customerId,
                order.orderDate,
                order.status,
                order.createdAt.toEpochMilli(),
                order.updatedAt.toEpochMilli()
            )
        )
    }

    override fun updateSaleOrder(
        id: Long,
        updateSaleOrderInputVO: UpdateSaleOrderInputVO
    ): ResponseEntity<SaleOrderVO> {
        val order = saleOrderService.updateSaleOrder(
            id,
            UpdateSaleOrderInputTO(
                customerId = updateSaleOrderInputVO.customerId,
                orderDate = updateSaleOrderInputVO.orderDate,
                status = updateSaleOrderInputVO.status
            )
        )
        return ResponseEntity.ok(
            SaleOrderVO(
                order.id,
                order.customerId,
                order.orderDate,
                order.status,
                order.createdAt.toEpochMilli(),
                order.updatedAt.toEpochMilli()
            )
        )
    }

    override fun deleteSaleOrder(id: Long): ResponseEntity<SaleOrderVO> {
        saleOrderService.deleteSaleOrder()
        return ResponseEntity.ok().build()
    }
} 
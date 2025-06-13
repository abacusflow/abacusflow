package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.portal.web.api.SaleOrdersApi
import org.bruwave.abacusflow.portal.web.model.SaleOrderVO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class SaleOrderController(
    private val saleOrderService: SaleOrderService
): SaleOrdersApi {

    override fun listSaleOrders(): ResponseEntity<List<SaleOrderVO>> {
        return super.listSaleOrders()
    }

    override fun getSaleOrder(id: Long): ResponseEntity<SaleOrderVO> {
        return super.getSaleOrder(id)
    }

    override fun addSaleOrder(saleOrderVO: SaleOrderVO): ResponseEntity<SaleOrderVO> {
        return super.addSaleOrder(saleOrderVO)
    }

    override fun updateSaleOrder(id: Long, saleOrderVO: SaleOrderVO): ResponseEntity<SaleOrderVO> {
        return super.updateSaleOrder(id, saleOrderVO)
    }

    override fun deleteSaleOrder(id: Long): ResponseEntity<SaleOrderVO> {
        return super.deleteSaleOrder(id)
    }
} 
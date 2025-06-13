package org.bruwave.abacusflow.portal.web.transaction

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
        return super.listSaleOrders()
    }

    override fun getSaleOrder(id: Long): ResponseEntity<SaleOrderVO> {
        return super.getSaleOrder(id)
    }

    override fun addSaleOrder(createSaleOrderInputVO: CreateSaleOrderInputVO): ResponseEntity<SaleOrderVO> {
        return super.addSaleOrder(createSaleOrderInputVO)
    }

    override fun updateSaleOrder(
        id: Long,
        updateSaleOrderInputVO: UpdateSaleOrderInputVO
    ): ResponseEntity<SaleOrderVO> {
        return super.updateSaleOrder(id, updateSaleOrderInputVO)
    }

    override fun deleteSaleOrder(id: Long): ResponseEntity<SaleOrderVO> {
        return super.deleteSaleOrder(id)
    }
} 
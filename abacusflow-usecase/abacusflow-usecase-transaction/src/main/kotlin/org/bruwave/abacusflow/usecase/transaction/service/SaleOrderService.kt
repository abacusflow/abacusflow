package org.bruwave.abacusflow.usecase.transaction.service

import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO

interface SaleOrderService {
    /**
     * 创建销售订单（包含订单项）
     */
    fun createSaleOrder(input: CreateSaleOrderInputTO): SaleOrderTO

    /**
     * 根据 ID 获取订单详情
     */
    fun getSaleOrder(id: Long): SaleOrderTO

    /**
     * 获取所有销售订单
     */
    fun listSaleOrders(): List<BasicSaleOrderTO>

    /**
     * 完成订单（会触发领域事件用于库存扣减）
     */
    fun completeOrder(id: Long): SaleOrderTO

    /**
     * 取消订单
     */
    fun cancelOrder(id: Long): SaleOrderTO

    fun reverseOrder(id: Long): SaleOrderTO
}

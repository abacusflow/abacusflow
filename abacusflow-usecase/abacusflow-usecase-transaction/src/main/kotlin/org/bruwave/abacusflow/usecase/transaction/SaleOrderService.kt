package org.bruwave.abacusflow.usecase.transaction

interface SaleOrderService {

    /**
     * 创建销售订单（包含订单项）
     */
    fun createSaleOrder(order: SaleOrderTO): SaleOrderTO

    /**
     * 更新销售订单状态（不修改订单项）
     */
    fun updateSaleOrder(orderTO: SaleOrderTO): SaleOrderTO

    /**
     * 删除销售订单
     */
    fun deleteSaleOrder(orderTO: SaleOrderTO): SaleOrderTO

    /**
     * 根据 ID 获取订单详情
     */
    fun getSaleOrder(id: Long): SaleOrderTO

    /**
     * 获取所有销售订单
     */
    fun listSaleOrders(): List<SaleOrderTO>

    /**
     * 根据客户 ID 查询其销售订单
     */
    fun listSaleOrdersByCustomer(customerId: Long): List<SaleOrderTO>

    /**
     * 完成订单（会触发领域事件用于库存扣减）
     */
    fun completeOrder(id: Long): SaleOrderTO
}
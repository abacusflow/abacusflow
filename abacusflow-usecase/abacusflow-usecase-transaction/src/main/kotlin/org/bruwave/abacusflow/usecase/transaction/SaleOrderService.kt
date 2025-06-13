package org.bruwave.abacusflow.usecase.transaction

interface SaleOrderService {

    /**
     * 创建销售订单（包含订单项）
     */
    fun createSaleOrder(input: CreateSaleOrderInputTO): SaleOrderTO

    /**
     * 更新销售订单状态（不修改订单项）
     */
    fun updateSaleOrder(id: Long, input: UpdateSaleOrderInputTO): SaleOrderTO

    /**
     * 删除销售订单
     */
    fun deleteSaleOrder(id: Long): SaleOrderTO

    /**
     * 根据 ID 获取订单详情
     */
    fun getSaleOrder(id: Long): SaleOrderTO

    /**
     * 获取所有销售订单
     */
    fun listSaleOrders(): List<BasicSaleOrderTO>

    /**
     * 根据客户 ID 查询其销售订单
     */
    fun listSaleOrdersByCustomer(customerId: Long): List<BasicSaleOrderTO>

    /**
     * 完成订单（会触发领域事件用于库存扣减）
     */
    fun completeOrder(id: Long): SaleOrderTO
}
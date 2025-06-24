package org.bruwave.abacusflow.usecase.transaction.service

import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO

interface SaleOrderQueryService {
    fun listSaleOrders(): List<BasicSaleOrderTO>
    fun getSaleOrder(id: Long): SaleOrderTO
}

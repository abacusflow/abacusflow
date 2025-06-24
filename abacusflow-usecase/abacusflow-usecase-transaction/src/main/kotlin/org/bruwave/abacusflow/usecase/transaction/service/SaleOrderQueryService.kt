package org.bruwave.abacusflow.usecase.transaction.service

import org.bruwave.abacusflow.partner.Customer
import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreateSaleOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface SaleOrderQueryService {
    fun listSaleOrdersPage(
        pageable: Pageable,
        orderNo: UUID?,
        customerName: String?,
        status: String?,
        productName: String?
    ): Page<BasicSaleOrderTO>
    fun getSaleOrder(id: Long): SaleOrderTO
}

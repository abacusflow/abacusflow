package org.abacusflow.usecase.transaction.service

import org.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.abacusflow.usecase.transaction.SaleOrderTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.UUID

interface SaleOrderQueryService {
    fun listBasicSaleOrdersPage(
        pageable: Pageable,
        orderNo: UUID?,
        customerName: String?,
        status: String?,
        productName: String?,
        orderDate: LocalDate?,
    ): Page<BasicSaleOrderTO>

    fun getSaleOrder(id: Long): SaleOrderTO
}

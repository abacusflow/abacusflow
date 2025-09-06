package org.abacusflow.usecase.transaction.service

import org.abacusflow.usecase.transaction.BasicPurchaseOrderTO
import org.abacusflow.usecase.transaction.PurchaseOrderTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.time.LocalDate
import java.util.UUID

interface PurchaseOrderQueryService {
    fun listBasicPurchaseOrdersPage(
        pageable: Pageable,
        orderNo: UUID?,
        supplierName: String?,
        status: String?,
        productName: String?,
        serialNumber: String?,
        orderDate: LocalDate?,
    ): Page<BasicPurchaseOrderTO>

    fun getPurchaseOrder(id: Long): PurchaseOrderTO
}

package org.bruwave.abacusflow.usecase.transaction.service

import org.bruwave.abacusflow.usecase.transaction.BasicPurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.UUID

interface PurchaseOrderQueryService {
    fun listBasicPurchaseOrdersPage(
        pageable: Pageable,
        orderNo: UUID?,
        supplierName: String?,
        status: String?,
        productName: String?,
    ): Page<BasicPurchaseOrderTO>

    fun getPurchaseOrder(id: Long): PurchaseOrderTO
}

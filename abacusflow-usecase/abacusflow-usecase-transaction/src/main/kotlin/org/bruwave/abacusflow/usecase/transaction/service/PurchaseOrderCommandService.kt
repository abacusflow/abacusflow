package org.bruwave.abacusflow.usecase.transaction.service

import org.bruwave.abacusflow.usecase.transaction.BasicPurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.CreatePurchaseOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO

interface PurchaseOrderCommandService {
    fun createPurchaseOrder(input: CreatePurchaseOrderInputTO): PurchaseOrderTO

    fun getPurchaseOrder(id: Long): PurchaseOrderTO

    fun listPurchaseOrders(): List<BasicPurchaseOrderTO>

    fun completeOrder(id: Long): PurchaseOrderTO

    fun cancelOrder(id: Long): PurchaseOrderTO

    fun reverseOrder(id: Long): PurchaseOrderTO
}

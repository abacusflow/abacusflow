package org.bruwave.abacusflow.usecase.transaction.service

import org.bruwave.abacusflow.usecase.transaction.CreatePurchaseOrderInputTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO

interface PurchaseOrderCommandService {
    fun createPurchaseOrder(input: CreatePurchaseOrderInputTO): PurchaseOrderTO

    fun completeOrder(id: Long): PurchaseOrderTO

    fun cancelOrder(id: Long): PurchaseOrderTO

    fun reverseOrder(id: Long): PurchaseOrderTO
}

package org.bruwave.abacusflow.usecase.transaction

interface PurchaseOrderService {
    fun createPurchaseOrder(order: PurchaseOrderTO): PurchaseOrderTO
    fun updatePurchaseOrder(orderTO: PurchaseOrderTO): PurchaseOrderTO
    fun deletePurchaseOrder(orderTO: PurchaseOrderTO): PurchaseOrderTO
    fun getPurchaseOrder(id: Long): PurchaseOrderTO
    fun listPurchaseOrdersBySupplier(supplierId: Long): List<PurchaseOrderTO>
    fun listPurchaseOrders(): List<PurchaseOrderTO>
    fun completeOrder(id: Long): PurchaseOrderTO
} 
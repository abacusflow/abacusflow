package org.bruwave.abacusflow.usecase.transaction

interface PurchaseOrderService {
    fun createPurchaseOrder(input: CreatePurchaseOrderInputTO): PurchaseOrderTO

    fun updatePurchaseOrder(
        id: Long,
        input: UpdatePurchaseOrderInputTO,
    ): PurchaseOrderTO

    fun deletePurchaseOrder(id: Long): PurchaseOrderTO

    fun getPurchaseOrder(id: Long): PurchaseOrderTO

    fun listPurchaseOrders(): List<BasicPurchaseOrderTO>

    fun listPurchaseOrdersBySupplier(supplierId: Long): List<BasicPurchaseOrderTO>

    fun completeOrder(id: Long): PurchaseOrderTO
}

package org.bruwave.abacusflow.usecase.transaction

data class CreatePurchaseOrderInputTO(
    val supplierId: Long,
    val items: List<PurchaseItemInputTO>,
)

data class UpdatePurchaseOrderInputTO(
    val supplierId: Long,
    val items: List<PurchaseItemInputTO>,
)
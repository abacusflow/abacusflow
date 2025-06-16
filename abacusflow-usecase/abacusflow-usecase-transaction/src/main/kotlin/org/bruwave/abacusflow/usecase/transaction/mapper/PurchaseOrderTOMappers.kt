package org.bruwave.abacusflow.usecase.transaction.mapper

import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.bruwave.abacusflow.transaction.PurchaseOrderItem
import org.bruwave.abacusflow.usecase.transaction.BasicPurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderItemTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO


fun PurchaseOrder.toTO() = PurchaseOrderTO(
    id = id,
    supplierId = supplierId,
    status = status.name,
    items = items.map { it.toTO() },
    createdAt = createdAt,
    updatedAt = updatedAt,
    orderNo = orderNo
)

fun PurchaseOrder.toBasicTO(supplierName: String) = BasicPurchaseOrderTO(
    id = id,
    supplierName = supplierName,
    status = status.name,
    itemCount = items.size,
    createdAt = createdAt,
    orderNo = orderNo,
    totalAmount = totalAmount,
    totalQuantity = totalQuantity,
    orderDate = orderDate,
)

fun PurchaseOrderItem.toTO() = PurchaseOrderItemTO(
    id = id,
    productId = productId,
    quantity = quantity,
    unitPrice = unitPrice,
    subtotal = subtotal
)
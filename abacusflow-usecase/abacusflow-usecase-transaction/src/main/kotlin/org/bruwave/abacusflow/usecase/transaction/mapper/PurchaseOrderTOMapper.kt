package org.bruwave.abacusflow.usecase.transaction.mapper

import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.bruwave.abacusflow.transaction.PurchaseOrderItem
import org.bruwave.abacusflow.usecase.transaction.BasicPurchaseOrderTO
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO

fun PurchaseOrder.toTO() =
    PurchaseOrderTO(
        id = id,
        orderNo = no,
        supplierId = supplierId,
        status = status.name,
        items = items.map { it.toTO() },
        orderDate = orderDate,
        note = note,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun PurchaseOrder.toBasicTO(supplierName: String) =
    BasicPurchaseOrderTO(
        id = id,
        supplierName = supplierName,
        status = status.name,
        itemCount = items.size,
        createdAt = createdAt,
        orderNo = no,
        totalAmount = totalAmount,
        totalQuantity = totalQuantity,
        orderDate = orderDate,
    )

fun PurchaseOrderItem.toTO() =
    PurchaseOrderTO.PurchaseOrderItemTO(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice,
        subtotal = subtotal,
    )

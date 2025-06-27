package org.bruwave.abacusflow.usecase.transaction.mapper

import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.bruwave.abacusflow.transaction.PurchaseOrderItem
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

fun PurchaseOrderItem.toTO() =
    PurchaseOrderTO.PurchaseOrderItemTO(
        id = id,
        productId = productId,
        quantity = quantity,
        unitPrice = unitPrice,
        subtotal = subtotal,
        serialNumber = serialNumber,
    )

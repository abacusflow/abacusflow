package org.bruwave.abacusflow.usecase.transaction.mapper

import org.bruwave.abacusflow.transaction.OrderStatus
import org.bruwave.abacusflow.transaction.SaleOrder
import org.bruwave.abacusflow.transaction.SaleOrderItem
import org.bruwave.abacusflow.usecase.transaction.BasicSaleOrderTO
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

fun SaleOrder.toTO() =
    SaleOrderTO(
        id = id,
        customerId = customerId,
        status = status.name,
        items = items.map { it.toTO() },
        createdAt = createdAt,
        updatedAt = updatedAt,
        orderNo = no,
        orderDate = orderDate,
        note = note,
    )

fun SaleOrderItem.toTO() =
    SaleOrderTO.SaleOrderItemTO(
        id = id,
        quantity = quantity,
        unitPrice = unitPrice,
        discountedPrice = discountedPrice,
        subtotal = subtotal,
        inventoryUnitId = inventoryUnitId,
    )

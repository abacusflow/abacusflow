package org.bruwave.abacusflow.usecase.warehouse

import org.bruwave.abacusflow.warehouse.Warehouse


fun Warehouse.toTO() = WarehouseTO(
    id = id,
    name = name,
    location = location,
    capacity = capacity,
    createdAt = createdAt,
    updatedAt = updatedAt,
    enabled = enabled
)

fun Warehouse.toBasicTO() = BasicWarehouseTO(
    id = id,
    name = name,
    location = location,
    capacity = capacity,
    enabled = enabled
)
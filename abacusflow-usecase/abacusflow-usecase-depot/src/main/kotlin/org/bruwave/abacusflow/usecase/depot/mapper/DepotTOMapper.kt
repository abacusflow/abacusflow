package org.bruwave.abacusflow.usecase.depot.mapper

import org.bruwave.abacusflow.depot.Depot
import org.bruwave.abacusflow.usecase.depot.BasicDepotTO
import org.bruwave.abacusflow.usecase.depot.DepotTO

fun Depot.toTO() =
    DepotTO(
        id = id,
        name = name,
        location = location,
        capacity = capacity,
        createdAt = createdAt,
        updatedAt = updatedAt,
        enabled = enabled,
    )

fun Depot.toBasicTO() =
    BasicDepotTO(
        id = id,
        name = name,
        location = location,
        capacity = capacity,
        enabled = enabled,
    )

package org.bruwave.abacusflow.usecase.depot

import org.bruwave.abacusflow.depot.Depot

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

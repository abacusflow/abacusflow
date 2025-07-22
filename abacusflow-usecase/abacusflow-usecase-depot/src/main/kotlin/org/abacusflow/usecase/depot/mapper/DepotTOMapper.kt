package org.abacusflow.usecase.depot.mapper

import org.abacusflow.depot.Depot
import org.abacusflow.usecase.depot.BasicDepotTO
import org.abacusflow.usecase.depot.DepotTO

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

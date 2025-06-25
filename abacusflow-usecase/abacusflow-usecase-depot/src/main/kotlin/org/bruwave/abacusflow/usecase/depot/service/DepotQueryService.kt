package org.bruwave.abacusflow.usecase.depot.service

import org.bruwave.abacusflow.usecase.depot.BasicDepotTO
import org.bruwave.abacusflow.usecase.depot.DepotTO

interface DepotQueryService {
    fun getDepot(id: Long): DepotTO

    fun listDepots(): List<BasicDepotTO>
}

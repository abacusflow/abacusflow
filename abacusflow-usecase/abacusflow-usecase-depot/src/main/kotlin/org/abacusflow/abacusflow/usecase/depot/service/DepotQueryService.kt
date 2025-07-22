package org.abacusflow.usecase.depot.service

import org.abacusflow.usecase.depot.BasicDepotTO
import org.abacusflow.usecase.depot.DepotTO

interface DepotQueryService {
    fun getDepot(id: Long): DepotTO

    fun listBasicDepots(): List<BasicDepotTO>
}

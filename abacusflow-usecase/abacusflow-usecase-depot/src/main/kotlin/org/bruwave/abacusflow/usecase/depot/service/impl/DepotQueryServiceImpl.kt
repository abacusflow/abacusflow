package org.bruwave.abacusflow.usecase.depot.service.impl

import org.bruwave.abacusflow.db.depot.DepotRepository
import org.bruwave.abacusflow.usecase.depot.BasicDepotTO
import org.bruwave.abacusflow.usecase.depot.DepotTO
import org.bruwave.abacusflow.usecase.depot.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.depot.mapper.toTO
import org.bruwave.abacusflow.usecase.depot.service.DepotQueryService
import org.springframework.stereotype.Service

@Service
class DepotQueryServiceImpl(
    private val depotRepository: DepotRepository,
) : DepotQueryService {
    override fun listBasicDepots(): List<BasicDepotTO> = depotRepository.findAll().map { it.toBasicTO() }

    override fun getDepot(id: Long): DepotTO =
        depotRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Depot not found with id: $id") }
            .toTO()
}

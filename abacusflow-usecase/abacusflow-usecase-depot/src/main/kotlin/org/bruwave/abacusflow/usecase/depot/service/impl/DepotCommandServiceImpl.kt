package org.bruwave.abacusflow.usecase.depot.service.impl

import org.bruwave.abacusflow.db.depot.DepotRepository
import org.bruwave.abacusflow.depot.Depot
import org.bruwave.abacusflow.usecase.depot.BasicDepotTO
import org.bruwave.abacusflow.usecase.depot.CreateDepotInputTO
import org.bruwave.abacusflow.usecase.depot.service.DepotCommandService
import org.bruwave.abacusflow.usecase.depot.DepotTO
import org.bruwave.abacusflow.usecase.depot.UpdateDepotInputTO
import org.bruwave.abacusflow.usecase.depot.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.depot.mapper.toTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DepotCommandServiceImpl(
    private val depotRepository: DepotRepository,
) : DepotCommandService {
    override fun createDepot(input: CreateDepotInputTO): DepotTO {
        val newDepot =
            Depot(
                name = input.name,
                location = input.location,
                capacity = input.capacity,
            )

        return depotRepository.save(newDepot).toTO()
    }

    override fun updateDepot(
        id: Long,
        input: UpdateDepotInputTO,
    ): DepotTO {
        val depot =
            depotRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Depot not found with id: $id") }

        depot.updateDepotInfo(
            newName = input.name,
            newLocation = input.location,
            newCapacity = input.capacity,
        )

        return depotRepository.save(depot).toTO()
    }

    override fun deleteDepot(id: Long): DepotTO {
        val depot =
            depotRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Depot not found with id: $id") }

        depotRepository.delete(depot)
        return depot.toTO()
    }
}

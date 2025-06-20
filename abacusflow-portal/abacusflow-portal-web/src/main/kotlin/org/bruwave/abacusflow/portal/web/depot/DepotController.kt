package org.bruwave.abacusflow.portal.web.depot

import org.bruwave.abacusflow.portal.web.api.DepotsApi
import org.bruwave.abacusflow.portal.web.model.BasicDepotVO
import org.bruwave.abacusflow.portal.web.model.CreateDepotInputVO
import org.bruwave.abacusflow.portal.web.model.DepotVO
import org.bruwave.abacusflow.portal.web.model.UpdateDepotInputVO
import org.bruwave.abacusflow.usecase.depot.CreateDepotInputTO
import org.bruwave.abacusflow.usecase.depot.DepotService
import org.bruwave.abacusflow.usecase.depot.UpdateDepotInputTO
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class DepotController(
    private val depotService: DepotService,
) : DepotsApi {
    override fun listDepots(): ResponseEntity<List<BasicDepotVO>> {
        val depotVOs =
            depotService.listDepots().map { depot ->
                depot.toBasicTO()
            }
        return ResponseEntity.ok(depotVOs)
    }

    override fun getDepot(id: Long): ResponseEntity<DepotVO> {
        val depot = depotService.getDepot(id)
        return ResponseEntity.ok(
            depot.toTO(),
        )
    }

    override fun addDepot(createDepotInputVO: CreateDepotInputVO): ResponseEntity<DepotVO> {
        val depot =
            depotService.createDepot(
                CreateDepotInputTO(
                    createDepotInputVO.name,
                    createDepotInputVO.location,
                    createDepotInputVO.capacity,
                ),
            )
        return ResponseEntity.ok(
            depot.toTO(),
        )
    }

    override fun updateDepot(
        id: Long,
        updateDepotInputVO: UpdateDepotInputVO,
    ): ResponseEntity<DepotVO> {
        val depot =
            depotService.updateDepot(
                id,
                UpdateDepotInputTO(
                    name = updateDepotInputVO.name,
                    location = updateDepotInputVO.location,
                    capacity = updateDepotInputVO.capacity,
                ),
            )
        return ResponseEntity.ok(
            depot.toTO(),
        )
    }

    override fun deleteDepot(id: Long): ResponseEntity<Unit> {
        depotService.deleteDepot(id)
        return ResponseEntity.ok().build()
    }
}

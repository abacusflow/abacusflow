package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.api.InventoryUnitsApi
import org.bruwave.abacusflow.portal.web.api.NotFoundException
import org.bruwave.abacusflow.portal.web.model.AssignInventoryUnitDepotRequestVO
import org.bruwave.abacusflow.portal.web.model.InventoryUnitStatusVO
import org.bruwave.abacusflow.portal.web.model.InventoryUnitVO
import org.bruwave.abacusflow.portal.web.model.SelectableInventoryUnitVO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryUnitCommandService
import org.bruwave.abacusflow.usecase.inventory.service.InventoryUnitQueryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryUnitController(
    private val inventoryUnitQueryService: InventoryUnitQueryService,
    private val inventoryUnitCommandService: InventoryUnitCommandService,
) : InventoryUnitsApi {
    override fun listSelectableInventoryUnits(statuses: List<InventoryUnitStatusVO>?): ResponseEntity<List<SelectableInventoryUnitVO>> {
        val unitVOS =
            inventoryUnitQueryService.listInventoryUnitsWithTitle(
                statuses?.map { it.name },
            ).map { unit ->
                SelectableInventoryUnitVO(
                    id = unit.id,
                    type = mapInventoryUnitTypeTOToVO(unit.type),
                    title = unit.title,
                    status = mapInventoryUnitStatusTOToVO(unit.status),
                )
            }
        return ResponseEntity.ok(unitVOS)
    }

    override fun getInventoryUnit(id: Long): ResponseEntity<InventoryUnitVO> {
        val inventory =
            inventoryUnitQueryService.getInventoryUnit(id)
                ?: throw NotFoundException("Could not find inventory unit for id $id")
        return ResponseEntity.ok(
            inventory.toVO(),
        )
    }

    override fun assignInventoryUnitDepot(
        id: Long,
        assignInventoryUnitDepotRequestVO: AssignInventoryUnitDepotRequestVO,
    ): ResponseEntity<Unit> {
        inventoryUnitCommandService.assignDepot(id, assignInventoryUnitDepotRequestVO.depotId)
        return ResponseEntity.ok().build()
    }
}

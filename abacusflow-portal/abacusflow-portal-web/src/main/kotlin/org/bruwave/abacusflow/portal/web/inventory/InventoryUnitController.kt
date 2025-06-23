package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.api.InventoryUnitsApi
import org.bruwave.abacusflow.portal.web.model.BasicInventoryUnitVO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryUnitService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryUnitController(
    private val inventoryUnitService: InventoryUnitService,
) : InventoryUnitsApi {
    override fun listInventoryUnits(): ResponseEntity<List<BasicInventoryUnitVO>> {
        val unitVOS = inventoryUnitService.listInventoryUnits().map { unit -> unit.toBasicVO() }
        return ResponseEntity.ok(unitVOS)
    }
}

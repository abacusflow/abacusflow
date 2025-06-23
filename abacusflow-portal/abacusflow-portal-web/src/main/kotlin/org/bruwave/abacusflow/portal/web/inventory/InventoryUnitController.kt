package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.api.InventoriesApi
import org.bruwave.abacusflow.portal.web.api.InventoryUnitsApi
import org.bruwave.abacusflow.portal.web.model.AdjustWarningLineRequestVO
import org.bruwave.abacusflow.portal.web.model.AssignDepotRequestVO
import org.bruwave.abacusflow.portal.web.model.BasicInventoryUnitVO
import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.IncreaseInventoryRequestVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.portal.web.model.ReleaseInventoryRequestVO
import org.bruwave.abacusflow.portal.web.model.ReserveInventoryRequestVO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryService
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

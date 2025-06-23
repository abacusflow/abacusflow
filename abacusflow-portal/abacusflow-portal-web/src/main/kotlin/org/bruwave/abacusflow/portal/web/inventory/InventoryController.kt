package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.api.InventoriesApi
import org.bruwave.abacusflow.portal.web.model.AdjustWarningLineRequestVO
import org.bruwave.abacusflow.portal.web.model.AssignDepotRequestVO
import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.IncreaseInventoryRequestVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.portal.web.model.ReleaseInventoryRequestVO
import org.bruwave.abacusflow.portal.web.model.ReserveInventoryRequestVO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryController(
    private val inventoryService: InventoryService,
) : InventoriesApi {
    override fun listInventories(): ResponseEntity<List<BasicInventoryVO>> {
        val inventoryVOs =
            inventoryService.listInventories().map { inventory ->
                inventory.toBasicVO()
            }
        return ResponseEntity.ok(inventoryVOs)
    }

    override fun getInventory(id: Long): ResponseEntity<InventoryVO> {
        val inventory = inventoryService.getInventory(id)
        return ResponseEntity.ok(
            inventory.toVO(),
        )
    }

    override fun adjustWarningLine(
        id: Long,
        adjustWarningLineRequestVO: AdjustWarningLineRequestVO,
    ): ResponseEntity<Unit> {
        inventoryService.adjustWarningLine(
            id,
            adjustWarningLineRequestVO.safetyStock,
            adjustWarningLineRequestVO.maxStock,
        )
        return ResponseEntity.ok().build()
    }

    override fun checkSafetyStock(id: Long): ResponseEntity<Boolean> {
        val isSafed = inventoryService.checkSafetyStock(id)
        return ResponseEntity.ok(isSafed)
    }
}

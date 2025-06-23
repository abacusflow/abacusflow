package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.api.InventoriesApi
import org.bruwave.abacusflow.portal.web.model.AdjustWarningLineRequestVO
import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryCommandService
import org.bruwave.abacusflow.usecase.inventory.service.InventoryQueryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryController(
    private val inventoryCommandService: InventoryCommandService,
    private val inventoryQueryService: InventoryQueryService,
) : InventoriesApi {
    override fun listInventories(): ResponseEntity<List<BasicInventoryVO>> {
        val inventoryVOs =
            inventoryQueryService.listInventories().map { inventory ->
                inventory.toBasicVO()
            }
        return ResponseEntity.ok(inventoryVOs)
    }

    override fun getInventory(id: Long): ResponseEntity<InventoryVO> {
        val inventory = inventoryQueryService.getInventory(id)
        return ResponseEntity.ok(
            inventory.toVO(),
        )
    }

    override fun adjustWarningLine(
        id: Long,
        adjustWarningLineRequestVO: AdjustWarningLineRequestVO,
    ): ResponseEntity<Unit> {
        inventoryCommandService.adjustWarningLine(
            id,
            adjustWarningLineRequestVO.safetyStock,
            adjustWarningLineRequestVO.maxStock,
        )
        return ResponseEntity.ok().build()
    }

    override fun checkSafetyStock(id: Long): ResponseEntity<Boolean> {
        val isSafed = inventoryCommandService.checkSafetyStock(id)
        return ResponseEntity.ok(isSafed)
    }
}

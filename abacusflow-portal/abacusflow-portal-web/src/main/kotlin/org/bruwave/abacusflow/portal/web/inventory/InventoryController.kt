package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.api.InventoriesApi
import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.IncreaseInventoryRequestVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.usecase.inventory.CreateInventoryInputTO
import org.bruwave.abacusflow.usecase.inventory.InventoriesService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryController(
    private val inventoriesService: InventoriesService
) : InventoriesApi {

    override fun listInventories(): ResponseEntity<List<BasicInventoryVO>> {
        val inventoryVOs = inventoriesService.listInventories().map { inventory ->
            inventory.toBasicVO()
        }
        return ResponseEntity.ok(inventoryVOs)
    }

    override fun getInventory(id: Long): ResponseEntity<InventoryVO> {
        val inventory = inventoriesService.getInventory(id)
        return ResponseEntity.ok(
            inventory.toVO()
        )
    }

    override fun increaseInventory(
        id: Long,
        increaseInventoryRequestVO: IncreaseInventoryRequestVO
    ): ResponseEntity<InventoryVO> {
        val inventory = inventoriesService.increaseInventory(id, increaseInventoryRequestVO.amount)
        return ResponseEntity.ok(
            inventory.toVO()
        )
    }

    override fun decreaseInventory(
        id: Long,
        increaseInventoryRequestVO: IncreaseInventoryRequestVO
    ): ResponseEntity<InventoryVO> {
        val inventory = inventoriesService.decreaseInventory(id, increaseInventoryRequestVO.amount)
        return ResponseEntity.ok(
            inventory.toVO()
        )
    }

    override fun checkSafetyStock(id: Long): ResponseEntity<Boolean> {
        val isSafed = inventoriesService.checkSafetyStock(id)
        return ResponseEntity.ok(isSafed)
    }
}
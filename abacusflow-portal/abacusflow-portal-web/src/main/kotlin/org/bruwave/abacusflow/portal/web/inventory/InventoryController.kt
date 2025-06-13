package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.api.InventoriesApi
import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.CreateInventoryInputVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.usecase.inventory.CreateInventoryInputTO
import org.bruwave.abacusflow.usecase.inventory.InventoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryController(
    private val inventoryService: InventoryService
) : InventoriesApi {

    override fun listInventories(): ResponseEntity<List<BasicInventoryVO>> {
        val inventoryVOs = inventoryService.listInventories().map { inventory ->
            inventory.toBasicVO()
        }
        return ResponseEntity.ok(inventoryVOs)
    }

    override fun getInventory(id: Long): ResponseEntity<InventoryVO> {
        val inventory = inventoryService.getInventory(id)
        return ResponseEntity.ok(
            inventory.toVO()
        )
    }

    override fun addInventory(createInventoryInputVO: CreateInventoryInputVO): ResponseEntity<InventoryVO> {
        val inventory = inventoryService.createInventory(
            CreateInventoryInputTO(
                createInventoryInputVO.productId,
                createInventoryInputVO.warehouseId,
                createInventoryInputVO.quantity,
                createInventoryInputVO.safetyStock,
                createInventoryInputVO.maxStock
            )
        )
        return ResponseEntity.ok(
            inventory.toVO()
        )
    }
}
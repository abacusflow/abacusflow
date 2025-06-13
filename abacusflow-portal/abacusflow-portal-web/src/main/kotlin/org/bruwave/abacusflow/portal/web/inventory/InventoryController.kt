package org.bruwave.abacusflow.portal.web.inventory

import org.bruwave.abacusflow.portal.web.api.InventoriesApi
import org.bruwave.abacusflow.portal.web.model.BasicInventoryVO
import org.bruwave.abacusflow.portal.web.model.CreateInventoryInputVO
import org.bruwave.abacusflow.portal.web.model.InventoryVO
import org.bruwave.abacusflow.portal.web.model.UpdateInventoryInputVO
import org.bruwave.abacusflow.usecase.inventory.InventoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class InventoryController(
    private val inventoryService: InventoryService
): InventoriesApi {

    override fun listInventories(): ResponseEntity<List<BasicInventoryVO>> {
        val inventories = inventoryService.listInventories()
        val inventoryVOs = inventories.map { inventory ->
            BasicInventoryVO(
                inventory.id,
                inventory.productId,
                inventory.warehouseId,
                inventory.quantity,
                inventory.safetyStock
            )
        }
        return ResponseEntity.ok(inventoryVOs)
    }

    override fun getInventory(id: Long): ResponseEntity<InventoryVO> {
        val inventory = inventoryService.getInventory(id)
        return ResponseEntity.ok(
            InventoryVO(
                inventory.id,
                inventory.productId,
                inventory.warehouseId,
                inventory.quantity,
                inventory.safetyStock,
                inventory.version,
                inventory.createdAt.toEpochMilli(),
                inventory.updatedAt.toEpochMilli()
            )
        )
    }

    override fun addInventory(createInventoryInputVO: CreateInventoryInputVO): ResponseEntity<InventoryVO> {
        val inventory = inventoryService.createInventory(
            CreateInventoryInputTO(
                createInventoryInputVO.productId,
                createInventoryInputVO.warehouseId,
                createInventoryInputVO.quantity,
                createInventoryInputVO.safetyStock
            )
        )
        return ResponseEntity.ok(
            InventoryVO(
                inventory.id,
                inventory.productId,
                inventory.warehouseId,
                inventory.quantity,
                inventory.safetyStock,
                inventory.version,
                inventory.createdAt.toEpochMilli(),
                inventory.updatedAt.toEpochMilli()
            )
        )
    }

    override fun updateInventory(
        id: Long,
        updateInventoryInputVO: UpdateInventoryInputVO
    ): ResponseEntity<InventoryVO> {
        val inventory = inventoryService.updateInventory(
            id,
            UpdateInventoryInputTO(
                quantity = updateInventoryInputVO.quantity,
                safetyStock = updateInventoryInputVO.safetyStock
            )
        )
        return ResponseEntity.ok(
            InventoryVO(
                inventory.id,
                inventory.productId,
                inventory.warehouseId,
                inventory.quantity,
                inventory.safetyStock,
                inventory.version,
                inventory.createdAt.toEpochMilli(),
                inventory.updatedAt.toEpochMilli()
            )
        )
    }

    override fun deleteInventory(id: Long): ResponseEntity<InventoryVO> {
        inventoryService.deleteInventory(id)
        return ResponseEntity.ok().build()
    }
}
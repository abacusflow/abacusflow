package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderService
import org.bruwave.abacusflow.usecase.transaction.PurchaseOrderTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/purchase-orders")
class PurchaseOrderController(private val purchaseOrderService: PurchaseOrderService) {

    @GetMapping
    fun listPurchaseOrders(): List<PurchaseOrderTO> = purchaseOrderService.listPurchaseOrders()

    @PostMapping
    fun createPurchaseOrder(@RequestBody purchaseOrder: PurchaseOrderTO): PurchaseOrderTO = purchaseOrderService.createPurchaseOrder(purchaseOrder)

    @GetMapping("/{id}")
    fun getPurchaseOrder(@PathVariable id: Long): PurchaseOrderTO = purchaseOrderService.getPurchaseOrder(id)

    @PutMapping("/{id}")
    fun updatePurchaseOrder(@PathVariable id: Long, @RequestBody purchaseOrderTO: PurchaseOrderTO): PurchaseOrderTO {
        return purchaseOrderService.updatePurchaseOrder(purchaseOrderTO.copy(id = id))
    }

    @DeleteMapping("/{id}")
    fun deletePurchaseOrder(@PathVariable id: Long): PurchaseOrderTO {
        return purchaseOrderService.deletePurchaseOrder(PurchaseOrderTO(id = id, supplierId = 0, items = emptyList(), createdAt = java.time.Instant.now(), updatedAt = java.time.Instant.now()))
    }
} 
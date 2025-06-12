package org.bruwave.abacusflow.portal.web

import org.bruwave.abacusflow.usecase.transaction.SaleOrderService
import org.bruwave.abacusflow.usecase.transaction.SaleOrderTO
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/sale-orders")
class SaleOrderController(private val saleOrderService: SaleOrderService) {

    @GetMapping
    fun listSaleOrders(): List<SaleOrderTO> = saleOrderService.listSaleOrders()

    @PostMapping
    fun createSaleOrder(@RequestBody saleOrder: SaleOrderTO): SaleOrderTO = saleOrderService.createSaleOrder(saleOrder)

    @GetMapping("/{id}")
    fun getSaleOrder(@PathVariable id: Long): SaleOrderTO = saleOrderService.getSaleOrder(id)

    @PutMapping("/{id}")
    fun updateSaleOrder(@PathVariable id: Long, @RequestBody saleOrderTO: SaleOrderTO): SaleOrderTO {
        return saleOrderService.updateSaleOrder(saleOrderTO.copy(id = id))
    }

    @DeleteMapping("/{id}")
    fun deleteSaleOrder(@PathVariable id: Long): SaleOrderTO {
        return saleOrderService.deleteSaleOrder(SaleOrderTO(id = id, customerId = 0, orderDate = java.time.Instant.now(), status = "", createdAt = java.time.Instant.now(), updatedAt = java.time.Instant.now()))
    }
} 
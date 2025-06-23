package org.bruwave.abacusflow.usecase.inventory.service.impl

import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORIES
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORY_UNIT
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCTS
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDERS
import org.bruwave.abacusflow.inventory.Inventory
import org.bruwave.abacusflow.inventory.InventoryUnit
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.transaction.PurchaseOrder
import org.bruwave.abacusflow.transaction.SaleOrder
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryQueryService
import org.bruwave.abacusflow.usecase.inventory.toBasicTO
import org.jooq.DSLContext
import org.jooq.Select
import org.jooq.impl.DSL
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class InventoryQueryServiceImpl(
    private val inventoryRepository: InventoryRepository,
    private val dslContext: DSLContext
) : InventoryQueryService {
    override fun listInventories(): List<BasicInventoryTO> {

        val result = dslContext
            .select(
                INVENTORIES.ID,
                INVENTORIES.PRODUCT_ID,
                PRODUCTS.NAME,
                INVENTORY_UNIT.ID,
                INVENTORY_UNIT.QUANTITY,
                INVENTORY_UNIT.PURCHASE_ORDER_ID,
                PURCHASE_ORDERS.NO,
                INVENTORY_UNIT.SALE_ORDER_IDS_MUTABLE
            )
            .from(INVENTORIES)
            .leftJoin(INVENTORY_UNIT).on(INVENTORIES.ID.eq(INVENTORY_UNIT.INVENTORY_ID))
            .leftJoin(PRODUCTS).on(INVENTORIES.PRODUCT_ID.eq(PRODUCTS.ID))
            .leftJoin(PURCHASE_ORDERS).on(INVENTORY_UNIT.PURCHASE_ORDER_ID.eq(PURCHASE_ORDERS.ID))
            .fetch()

        return
    }


    override fun getInventory(id: Long): InventoryTO {
        TODO("Not yet implemented")
    }
}

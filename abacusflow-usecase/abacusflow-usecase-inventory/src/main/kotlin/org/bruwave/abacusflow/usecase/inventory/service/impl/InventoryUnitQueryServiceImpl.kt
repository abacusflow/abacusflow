package org.bruwave.abacusflow.usecase.inventory.service.impl

import org.bruwave.abacusflow.db.inventory.InventoryUnitRepository
import org.bruwave.abacusflow.generated.jooq.Tables.DEPOTS
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORIES
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORY_UNIT
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCTS
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDERS
import org.bruwave.abacusflow.generated.jooq.Tables.SALE_ORDERS
import org.bruwave.abacusflow.inventory.InventoryUnit
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryUnitQueryService
import org.bruwave.abacusflow.usecase.inventory.toTO
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class InventoryUnitQueryServiceImpl(
    private val inventoryUnitRepository: InventoryUnitRepository,
    private val dslContext: DSLContext,
) : InventoryUnitQueryService {
    override fun listInventoryUnits(): List<BasicInventoryUnitTO> {
        // 使用 JOOQ 执行联接查询
        val inventoryUnits =
            dslContext
                .select(
                    INVENTORY_UNIT.ID,
                    INVENTORY_UNIT.UNIT_TYPE,
                    INVENTORY_UNIT.BATCH_CODE,
                    INVENTORY_UNIT.SERIAL_NUMBER,
                    INVENTORY_UNIT.PURCHASE_ORDER_ID,
                    INVENTORY_UNIT.SALE_ORDER_IDS,
                    DEPOTS.NAME,
                    INVENTORY_UNIT.QUANTITY,
                    INVENTORY_UNIT.REMAINING_QUANTITY,
                    INVENTORY_UNIT.UNIT_PRICE,
                    INVENTORY_UNIT.RECEIVED_AT,
                    INVENTORIES.PRODUCT_ID,
                    PRODUCTS.NAME,
                    PURCHASE_ORDERS.NO,
                    DSL.arrayAgg(SALE_ORDERS.NO).`as`("sale_order_nos"),
                )
                .from(INVENTORY_UNIT)
                .leftJoin(INVENTORIES).on(INVENTORIES.ID.eq(INVENTORY_UNIT.INVENTORY_ID)) // 关联 INVENTORY 表
                .leftJoin(PRODUCTS).on(PRODUCTS.ID.eq(INVENTORIES.PRODUCT_ID)) // 关联 Product 表
                .leftJoin(PURCHASE_ORDERS).on(PURCHASE_ORDERS.ID.eq(INVENTORY_UNIT.PURCHASE_ORDER_ID)) // 关联 PurchaseOrder 表
//            .leftJoin(SALE_ORDERS).on(SALE_ORDERS.ID.`in`(INVENTORY_UNIT.SALE_ORDER_IDS)) // 关联 SaleOrder 表
                .leftJoin(SALE_ORDERS).on(
                    DSL.condition("{0} = ANY({1})", SALE_ORDERS.ID, INVENTORY_UNIT.SALE_ORDER_IDS),
                )
                .leftJoin(DEPOTS).on(INVENTORY_UNIT.DEPOT_ID.eq(DEPOTS.ID))
                .groupBy(
                    INVENTORY_UNIT.ID,
                    INVENTORY_UNIT.UNIT_TYPE,
                    INVENTORY_UNIT.BATCH_CODE,
                    INVENTORY_UNIT.SERIAL_NUMBER,
                    INVENTORY_UNIT.PURCHASE_ORDER_ID,
                    INVENTORY_UNIT.SALE_ORDER_IDS,
                    DEPOTS.NAME,
                    INVENTORY_UNIT.QUANTITY,
                    INVENTORY_UNIT.REMAINING_QUANTITY,
                    INVENTORY_UNIT.UNIT_PRICE,
                    INVENTORY_UNIT.RECEIVED_AT,
                    INVENTORIES.PRODUCT_ID,
                    PRODUCTS.NAME,
                    PURCHASE_ORDERS.NO,
                )
                .fetch()

        // 转换查询结果为 BasicInventoryUnitTO
        return inventoryUnits.map { record ->
            record.toBasicInventoryUnitTO()
        }
    }

    override fun getInventoryUnit(id: Long): InventoryUnitTO? {
        return inventoryUnitRepository.findById(id)
            .orElseThrow { NoSuchElementException("Inventory not found with id: $id") }
            .toTO()
    }

    fun Record.toBasicInventoryUnitTO(): BasicInventoryUnitTO? {
        val id = this[INVENTORY_UNIT.ID] ?: return null

        val unitType: InventoryUnit.UnitType = InventoryUnit.UnitType.valueOf(this[INVENTORY_UNIT.UNIT_TYPE]!!)

        val title: String =
            when (unitType) {
                InventoryUnit.UnitType.BATCH -> "${this[PRODUCTS.NAME]}-批次号:-${this[INVENTORY_UNIT.BATCH_CODE]}"
                InventoryUnit.UnitType.INSTANCE -> "${this[PRODUCTS.NAME]}-序列号:-${this[INVENTORY_UNIT.SERIAL_NUMBER]}"
            }
        val saleOrderNos: List<UUID> =
            this.get("sale_order_nos", Array<UUID>::class.java)
                ?.toList() ?: emptyList()

        return BasicInventoryUnitTO(
            id = id,
            title = title,
            unitType = unitType.name, // 通常是枚举/字符串，如 "INSTANCE" 或 "BATCH"
            purchaseOrderNo = this[PURCHASE_ORDERS.NO]!!,
            saleOrderNos = saleOrderNos,
            depotName = this[DEPOTS.NAME],
            quantity = this[INVENTORY_UNIT.QUANTITY] ?: 0L,
            remainingQuantity = this[INVENTORY_UNIT.REMAINING_QUANTITY] ?: 0L,
            unitPrice = this[INVENTORY_UNIT.UNIT_PRICE] ?: 0.0,
            receivedAt = this[INVENTORY_UNIT.RECEIVED_AT]?.toInstant() ?: Instant.EPOCH,
            batchCode = this[INVENTORY_UNIT.BATCH_CODE],
            serialNumber = this[INVENTORY_UNIT.SERIAL_NUMBER],
        )
    }
}

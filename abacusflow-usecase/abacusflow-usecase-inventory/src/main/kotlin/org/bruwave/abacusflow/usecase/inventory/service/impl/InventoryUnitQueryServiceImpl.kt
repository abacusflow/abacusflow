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
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitWithTitleTO
import org.bruwave.abacusflow.usecase.inventory.mapper.toTO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryUnitQueryService
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Service
class InventoryUnitQueryServiceImpl(
    private val inventoryUnitRepository: InventoryUnitRepository,
    private val dslContext: DSLContext,
) : InventoryUnitQueryService {
    override fun listBasicInventoryUnits(statusList: List<String>?): List<BasicInventoryUnitTO> {
        val condition =
            buildList<Condition> {
                statusList?.let {
                    val upperCaseStatuses = statusList.map { it.uppercase() }
                    add(INVENTORY_UNIT.STATUS.`in`(upperCaseStatuses))
                }
            }

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
                    INVENTORY_UNIT.STATUS,
                    PRODUCTS.NAME,
                    PURCHASE_ORDERS.NO,
                    DSL.arrayAgg(SALE_ORDERS.NO).`as`("sale_order_nos"),
                )
                .from(INVENTORY_UNIT)
                .leftJoin(INVENTORIES).on(INVENTORIES.ID.eq(INVENTORY_UNIT.INVENTORY_ID)) // 关联 INVENTORY 表
                .leftJoin(PRODUCTS).on(PRODUCTS.ID.eq(INVENTORIES.PRODUCT_ID)) // 关联 Product 表
                .leftJoin(PURCHASE_ORDERS)
                .on(PURCHASE_ORDERS.ID.eq(INVENTORY_UNIT.PURCHASE_ORDER_ID)) // 关联 PurchaseOrder 表
//            .leftJoin(SALE_ORDERS).on(SALE_ORDERS.ID.`in`(INVENTORY_UNIT.SALE_ORDER_IDS)) // 关联 SaleOrder 表
                .leftJoin(SALE_ORDERS).on(
                    DSL.condition("{0} = ANY({1})", SALE_ORDERS.ID, INVENTORY_UNIT.SALE_ORDER_IDS),
                )
                .leftJoin(DEPOTS).on(INVENTORY_UNIT.DEPOT_ID.eq(DEPOTS.ID))
                .where(condition)
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
                    INVENTORY_UNIT.STATUS,
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

    override fun listInventoryUnits(): List<InventoryUnitTO> {
        val inventoryUnits = dslContext
            .select(
                INVENTORY_UNIT.ID,
                INVENTORY_UNIT.UNIT_TYPE,
                INVENTORY_UNIT.INVENTORY_ID,
                INVENTORY_UNIT.PURCHASE_ORDER_ID,
                INVENTORY_UNIT.QUANTITY,
                INVENTORY_UNIT.REMAINING_QUANTITY,
                INVENTORY_UNIT.UNIT_PRICE,
                INVENTORY_UNIT.DEPOT_ID,
                INVENTORY_UNIT.STATUS,
                INVENTORY_UNIT.SALE_ORDER_IDS,
                INVENTORY_UNIT.RECEIVED_AT,
                INVENTORY_UNIT.CREATED_AT,
                INVENTORY_UNIT.UPDATED_AT,
                INVENTORY_UNIT.SERIAL_NUMBER,
                INVENTORY_UNIT.BATCH_CODE
            )
            .from(INVENTORY_UNIT)
            .fetch()

        return inventoryUnits.map { record ->
            record.toInventoryUnitTO()
        }
    }

    override fun listInventoryUnitsWithTitle(statusList: List<String>?): List<InventoryUnitWithTitleTO> {
        val condition =
            buildList<Condition> {
                statusList?.let {
                    val upperCaseStatuses = statusList.map { it.uppercase() }
                    add(INVENTORY_UNIT.STATUS.`in`(upperCaseStatuses))
                }
            }

        val inventoryUnits = dslContext
            .select(
                INVENTORY_UNIT.ID,
                INVENTORY_UNIT.UNIT_TYPE,
                INVENTORY_UNIT.STATUS,
                INVENTORY_UNIT.BATCH_CODE,
                INVENTORY_UNIT.SERIAL_NUMBER,
                PRODUCTS.NAME
            )
            .distinctOn(INVENTORY_UNIT.ID)  // PostgreSQL 特有语法
            .from(INVENTORY_UNIT)
            .leftJoin(INVENTORIES).on(INVENTORIES.ID.eq(INVENTORY_UNIT.INVENTORY_ID)) // 关联 INVENTORY 表
            .leftJoin(PRODUCTS).on(PRODUCTS.ID.eq(INVENTORIES.PRODUCT_ID)) // 关联 Product 表
            .where(condition)
            .fetch()

        return inventoryUnits.map { record ->
            record.toInventoryUnitWithTitleTO()
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
            type = unitType.name, // 通常是枚举/字符串，如 "INSTANCE" 或 "BATCH"
            purchaseOrderNo = this[PURCHASE_ORDERS.NO]!!,
            saleOrderNos = saleOrderNos,
            depotName = this[DEPOTS.NAME],
            quantity = this[INVENTORY_UNIT.QUANTITY] ?: 0L,
            remainingQuantity = this[INVENTORY_UNIT.REMAINING_QUANTITY] ?: 0L,
            unitPrice = this[INVENTORY_UNIT.UNIT_PRICE] ?: BigDecimal.ZERO,
            receivedAt = this[INVENTORY_UNIT.RECEIVED_AT]?.toInstant() ?: Instant.EPOCH,
            batchCode = this[INVENTORY_UNIT.BATCH_CODE],
            serialNumber = this[INVENTORY_UNIT.SERIAL_NUMBER],
            status = this[INVENTORY_UNIT.STATUS],
        )
    }

    fun Record.toInventoryUnitTO(): InventoryUnitTO {
        val unitType: InventoryUnit.UnitType = InventoryUnit.UnitType.valueOf(this[INVENTORY_UNIT.UNIT_TYPE]!!)
        return InventoryUnitTO(
            id = this[INVENTORY_UNIT.ID] ?: throw NoSuchElementException("InventoryUnit ID is missing"),
            type = unitType.name,
            inventoryId = this[INVENTORY_UNIT.INVENTORY_ID] ?: throw NoSuchElementException("Inventory ID is missing"),
            purchaseOrderId = this[INVENTORY_UNIT.PURCHASE_ORDER_ID]
                ?: throw NoSuchElementException("Purchase Order ID is missing"),
            quantity = this[INVENTORY_UNIT.QUANTITY] ?: 0L,
            remainingQuantity = this[INVENTORY_UNIT.REMAINING_QUANTITY] ?: 0L,
            unitPrice = this[INVENTORY_UNIT.UNIT_PRICE] ?: BigDecimal.ZERO,
            depotId = this[INVENTORY_UNIT.DEPOT_ID],
            status = this[INVENTORY_UNIT.STATUS] ?: "UNKNOWN",
            saleOrderIds = this[INVENTORY_UNIT.SALE_ORDER_IDS]?.toList() ?: emptyList(),
            receivedAt = this[INVENTORY_UNIT.RECEIVED_AT]?.toInstant() ?: Instant.EPOCH,
            createdAt = this[INVENTORY_UNIT.CREATED_AT]?.toInstant() ?: Instant.EPOCH,
            updatedAt = this[INVENTORY_UNIT.UPDATED_AT]?.toInstant() ?: Instant.EPOCH,
            serialNumber = this[INVENTORY_UNIT.SERIAL_NUMBER],
            batchCode = this[INVENTORY_UNIT.BATCH_CODE]
        )
    }

    fun Record.toInventoryUnitWithTitleTO(): InventoryUnitWithTitleTO {
        val unitType: InventoryUnit.UnitType = InventoryUnit.UnitType.valueOf(this[INVENTORY_UNIT.UNIT_TYPE]!!)
        val title: String =
            when (unitType) {
                InventoryUnit.UnitType.BATCH -> "${this[PRODUCTS.NAME]}-批次号:-${this[INVENTORY_UNIT.BATCH_CODE]}"
                InventoryUnit.UnitType.INSTANCE -> "${this[PRODUCTS.NAME]}-序列号:-${this[INVENTORY_UNIT.SERIAL_NUMBER]}"
            }
        return InventoryUnitWithTitleTO(
            id = this[INVENTORY_UNIT.ID] ?: throw NoSuchElementException("InventoryUnit ID is missing"),
            type = unitType.name,
            title = title,
            status = this[INVENTORY_UNIT.STATUS] ?: "UNKNOWN",
        )
    }
}

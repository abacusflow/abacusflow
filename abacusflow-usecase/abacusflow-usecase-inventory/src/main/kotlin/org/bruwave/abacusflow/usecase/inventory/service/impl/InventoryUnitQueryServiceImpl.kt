package org.bruwave.abacusflow.usecase.inventory.service.impl

import org.bruwave.abacusflow.db.inventory.InventoryUnitRepository
import org.bruwave.abacusflow.generated.jooq.Tables.DEPOT
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORY
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORY_UNIT
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCT
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDER
import org.bruwave.abacusflow.generated.jooq.Tables.SALE_ORDER
import org.bruwave.abacusflow.inventory.InventoryUnit
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryUnitForExportTO
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
    override fun listBasicInventoryUnits(): List<BasicInventoryUnitTO> {
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
                    DEPOT.NAME,
                    INVENTORY_UNIT.INITIAL_QUANTITY,
                    INVENTORY_UNIT.QUANTITY,
                    INVENTORY_UNIT.FROZEN_QUANTITY,
                    INVENTORY_UNIT.UNIT_PRICE,
                    INVENTORY_UNIT.RECEIVED_AT,
                    INVENTORY.PRODUCT_ID,
                    INVENTORY_UNIT.STATUS,
                    PRODUCT.NAME,
                    PURCHASE_ORDER.NO,
                    DSL.arrayAgg(SALE_ORDER.NO).`as`("sale_order_nos"),
                )
                .from(INVENTORY_UNIT)
                .leftJoin(INVENTORY).on(INVENTORY.ID.eq(INVENTORY_UNIT.INVENTORY_ID)) // 关联 INVENTORY 表
                .leftJoin(PRODUCT).on(PRODUCT.ID.eq(INVENTORY.PRODUCT_ID)) // 关联 Product 表
                .leftJoin(PURCHASE_ORDER)
                .on(PURCHASE_ORDER.ID.eq(INVENTORY_UNIT.PURCHASE_ORDER_ID)) // 关联 PurchaseOrder 表
//            .leftJoin(SALE_ORDER).on(SALE_ORDER.ID.`in`(INVENTORY_UNIT.SALE_ORDER_IDS)) // 关联 SaleOrder 表
                .leftJoin(SALE_ORDER).on(
                    DSL.condition("{0} = ANY({1})", SALE_ORDER.ID, INVENTORY_UNIT.SALE_ORDER_IDS),
                )
                .leftJoin(DEPOT).on(INVENTORY_UNIT.DEPOT_ID.eq(DEPOT.ID))
                .groupBy(
                    INVENTORY_UNIT.ID,
                    INVENTORY_UNIT.UNIT_TYPE,
                    INVENTORY_UNIT.BATCH_CODE,
                    INVENTORY_UNIT.SERIAL_NUMBER,
                    INVENTORY_UNIT.PURCHASE_ORDER_ID,
                    INVENTORY_UNIT.SALE_ORDER_IDS,
                    DEPOT.NAME,
                    INVENTORY_UNIT.INITIAL_QUANTITY,
                    INVENTORY_UNIT.QUANTITY,
                    INVENTORY_UNIT.FROZEN_QUANTITY,
                    INVENTORY_UNIT.UNIT_PRICE,
                    INVENTORY_UNIT.RECEIVED_AT,
                    INVENTORY_UNIT.STATUS,
                    INVENTORY.PRODUCT_ID,
                    PRODUCT.NAME,
                    PURCHASE_ORDER.NO,
                )
                .orderBy(INVENTORY_UNIT.CREATED_AT.desc())
                .fetch()

        // 转换查询结果为 BasicInventoryUnitTO
        return inventoryUnits.map { record ->
            record.toBasicInventoryUnitTO()
        }
    }

    override fun listInventoryUnits(): List<InventoryUnitTO> {
        val inventoryUnits =
            dslContext
                .select(
                    INVENTORY_UNIT.ID,
                    INVENTORY_UNIT.UNIT_TYPE,
                    INVENTORY_UNIT.INVENTORY_ID,
                    INVENTORY_UNIT.PURCHASE_ORDER_ID,
                    INVENTORY_UNIT.INITIAL_QUANTITY,
                    INVENTORY_UNIT.QUANTITY,
                    INVENTORY_UNIT.FROZEN_QUANTITY,
                    INVENTORY_UNIT.UNIT_PRICE,
                    INVENTORY_UNIT.DEPOT_ID,
                    INVENTORY_UNIT.STATUS,
                    INVENTORY_UNIT.SALE_ORDER_IDS,
                    INVENTORY_UNIT.RECEIVED_AT,
                    INVENTORY_UNIT.CREATED_AT,
                    INVENTORY_UNIT.UPDATED_AT,
                    INVENTORY_UNIT.SERIAL_NUMBER,
                    INVENTORY_UNIT.BATCH_CODE,
                )
                .from(INVENTORY_UNIT)
                .orderBy(INVENTORY_UNIT.CREATED_AT.desc())
                .fetch()

        return inventoryUnits.map { record ->
            record.toInventoryUnitTO()
        }
    }

    override fun listInventoryUnitsForExport(): List<InventoryUnitForExportTO> {
        // 使用 JOOQ 执行联接查询
        val inventoryUnits =
            dslContext
                .select(
                    INVENTORY_UNIT.UNIT_TYPE,
                    INVENTORY_UNIT.BATCH_CODE,
                    INVENTORY_UNIT.SERIAL_NUMBER,
                    INVENTORY_UNIT.PURCHASE_ORDER_ID,
                    INVENTORY_UNIT.SALE_ORDER_IDS,
                    DEPOT.NAME,
                    INVENTORY_UNIT.INITIAL_QUANTITY,
                    INVENTORY_UNIT.QUANTITY,
                    INVENTORY_UNIT.FROZEN_QUANTITY,
                    INVENTORY_UNIT.UNIT_PRICE,
                    INVENTORY_UNIT.RECEIVED_AT,
                    INVENTORY.PRODUCT_ID,
                    INVENTORY_UNIT.STATUS,
                    PRODUCT.NAME,
                    PURCHASE_ORDER.NO,
                    DSL.arrayAgg(SALE_ORDER.NO).`as`("sale_order_nos"),
                )
                .from(INVENTORY_UNIT)
                .leftJoin(INVENTORY).on(INVENTORY.ID.eq(INVENTORY_UNIT.INVENTORY_ID)) // 关联 INVENTORY 表
                .leftJoin(PRODUCT).on(PRODUCT.ID.eq(INVENTORY.PRODUCT_ID)) // 关联 Product 表
                .leftJoin(PURCHASE_ORDER)
                .on(PURCHASE_ORDER.ID.eq(INVENTORY_UNIT.PURCHASE_ORDER_ID)) // 关联 PurchaseOrder 表
                .leftJoin(SALE_ORDER).on(
                    DSL.condition("{0} = ANY({1})", SALE_ORDER.ID, INVENTORY_UNIT.SALE_ORDER_IDS),
                )
                .leftJoin(DEPOT).on(INVENTORY_UNIT.DEPOT_ID.eq(DEPOT.ID))
                .where(INVENTORY_UNIT.QUANTITY.gt(0)) // ✅ 新增过滤条件
                .groupBy(
                    INVENTORY_UNIT.UNIT_TYPE,
                    INVENTORY_UNIT.BATCH_CODE,
                    INVENTORY_UNIT.SERIAL_NUMBER,
                    INVENTORY_UNIT.PURCHASE_ORDER_ID,
                    INVENTORY_UNIT.SALE_ORDER_IDS,
                    DEPOT.NAME,
                    INVENTORY_UNIT.INITIAL_QUANTITY,
                    INVENTORY_UNIT.QUANTITY,
                    INVENTORY_UNIT.FROZEN_QUANTITY,
                    INVENTORY_UNIT.UNIT_PRICE,
                    INVENTORY_UNIT.RECEIVED_AT,
                    INVENTORY_UNIT.STATUS,
                    INVENTORY.PRODUCT_ID,
                    PRODUCT.NAME,
                    PURCHASE_ORDER.NO,
                    INVENTORY_UNIT.CREATED_AT
                )
                .orderBy(INVENTORY_UNIT.CREATED_AT.desc())
                .fetch()

        // 转换查询结果
        return inventoryUnits.map { record ->
            record.toInventoryUnitForExportTO()
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

        val inventoryUnits =
            dslContext
                .selectDistinct(
                    INVENTORY_UNIT.ID,
                    INVENTORY_UNIT.UNIT_TYPE,
                    INVENTORY_UNIT.STATUS,
                    INVENTORY_UNIT.BATCH_CODE,
                    INVENTORY_UNIT.SERIAL_NUMBER,
                    PRODUCT.NAME,
                    INVENTORY_UNIT.CREATED_AT
                )
                .from(INVENTORY_UNIT)
                .leftJoin(INVENTORY).on(INVENTORY.ID.eq(INVENTORY_UNIT.INVENTORY_ID)) // 关联 INVENTORY 表
                .leftJoin(PRODUCT).on(PRODUCT.ID.eq(INVENTORY.PRODUCT_ID)) // 关联 Product 表
                .where(condition)
                .orderBy(INVENTORY_UNIT.CREATED_AT.desc())
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
                InventoryUnit.UnitType.BATCH -> "${this[PRODUCT.NAME]}-批次号:-${this[INVENTORY_UNIT.BATCH_CODE]}"
                InventoryUnit.UnitType.INSTANCE -> "${this[PRODUCT.NAME]}-序列号:-${this[INVENTORY_UNIT.SERIAL_NUMBER]}"
            }
        val saleOrderNos: List<UUID> =
            this.get("sale_order_nos", Array<UUID>::class.java)
                ?.toList() ?: emptyList()

        val quantity = this[INVENTORY_UNIT.QUANTITY] ?: 0L
        val frozenQuantity = this[INVENTORY_UNIT.FROZEN_QUANTITY] ?: 0L

        return BasicInventoryUnitTO(
            id = id,
            title = title,
            type = unitType.name, // 通常是枚举/字符串，如 "INSTANCE" 或 "BATCH"
            purchaseOrderNo = this[PURCHASE_ORDER.NO]!!,
            saleOrderNos = saleOrderNos,
            depotName = this[DEPOT.NAME],
            initialQuantity = this[INVENTORY_UNIT.INITIAL_QUANTITY] ?: 0L,
            quantity = quantity,
            remainingQuantity = quantity - frozenQuantity,
            unitPrice = this[INVENTORY_UNIT.UNIT_PRICE] ?: BigDecimal.ZERO,
            receivedAt = this[INVENTORY_UNIT.RECEIVED_AT]?.toInstant() ?: Instant.EPOCH,
            batchCode = this[INVENTORY_UNIT.BATCH_CODE],
            serialNumber = this[INVENTORY_UNIT.SERIAL_NUMBER],
            status = this[INVENTORY_UNIT.STATUS].name,
        )
    }

    fun Record.toInventoryUnitTO(): InventoryUnitTO {
        val unitType: InventoryUnit.UnitType = InventoryUnit.UnitType.valueOf(this[INVENTORY_UNIT.UNIT_TYPE]!!)

        val quantity = this[INVENTORY_UNIT.QUANTITY] ?: 0L
        val frozenQuantity = this[INVENTORY_UNIT.FROZEN_QUANTITY] ?: 0L

        return InventoryUnitTO(
            id = this[INVENTORY_UNIT.ID] ?: throw NoSuchElementException("InventoryUnit ID is missing"),
            type = unitType.name,
            inventoryId = this[INVENTORY_UNIT.INVENTORY_ID] ?: throw NoSuchElementException("Inventory ID is missing"),
            purchaseOrderId =
                this[INVENTORY_UNIT.PURCHASE_ORDER_ID]
                    ?: throw NoSuchElementException("Purchase Order ID is missing"),
            initialQuantity = this[INVENTORY_UNIT.INITIAL_QUANTITY] ?: 0L,
            quantity = quantity,
            remainingQuantity = quantity - frozenQuantity,
            unitPrice = this[INVENTORY_UNIT.UNIT_PRICE] ?: BigDecimal.ZERO,
            depotId = this[INVENTORY_UNIT.DEPOT_ID],
            status = this[INVENTORY_UNIT.STATUS].name,
            saleOrderIds = this[INVENTORY_UNIT.SALE_ORDER_IDS]?.toList() ?: emptyList(),
            receivedAt = this[INVENTORY_UNIT.RECEIVED_AT]?.toInstant() ?: Instant.EPOCH,
            createdAt = this[INVENTORY_UNIT.CREATED_AT]?.toInstant() ?: Instant.EPOCH,
            updatedAt = this[INVENTORY_UNIT.UPDATED_AT]?.toInstant() ?: Instant.EPOCH,
            serialNumber = this[INVENTORY_UNIT.SERIAL_NUMBER],
            batchCode = this[INVENTORY_UNIT.BATCH_CODE],
        )
    }

    fun Record.toInventoryUnitWithTitleTO(): InventoryUnitWithTitleTO {
        val unitType: InventoryUnit.UnitType = InventoryUnit.UnitType.valueOf(this[INVENTORY_UNIT.UNIT_TYPE]!!)
        val title: String =
            when (unitType) {
                InventoryUnit.UnitType.BATCH -> "${this[PRODUCT.NAME]}-批次号-${this[INVENTORY_UNIT.BATCH_CODE]}"
                InventoryUnit.UnitType.INSTANCE -> "${this[PRODUCT.NAME]}-序列号-${this[INVENTORY_UNIT.SERIAL_NUMBER]}"
            }
        return InventoryUnitWithTitleTO(
            id = this[INVENTORY_UNIT.ID] ?: throw NoSuchElementException("InventoryUnit ID is missing"),
            type = unitType.name,
            title = title,
            status = this[INVENTORY_UNIT.STATUS].name,
        )
    }


    fun Record.toInventoryUnitForExportTO(): InventoryUnitForExportTO? {
        val unitType: InventoryUnit.UnitType = InventoryUnit.UnitType.valueOf(this[INVENTORY_UNIT.UNIT_TYPE]!!)

        val title: String =
            when (unitType) {
                InventoryUnit.UnitType.BATCH -> "${this[PRODUCT.NAME]}-批次号:-${this[INVENTORY_UNIT.BATCH_CODE]}"
                InventoryUnit.UnitType.INSTANCE -> "${this[PRODUCT.NAME]}-序列号:-${this[INVENTORY_UNIT.SERIAL_NUMBER]}"
            }
        val saleOrderNos: List<UUID> =
            this.get("sale_order_nos", Array<UUID>::class.java)
                ?.toList() ?: emptyList()

        val quantity = this[INVENTORY_UNIT.QUANTITY] ?: 0L
        val frozenQuantity = this[INVENTORY_UNIT.FROZEN_QUANTITY] ?: 0L

        return InventoryUnitForExportTO(
            title = title,
            type = unitType.name, // 通常是枚举/字符串，如 "INSTANCE" 或 "BATCH"
            purchaseOrderNo = this[PURCHASE_ORDER.NO]!!,
            saleOrderNos = saleOrderNos,
            depotName = this[DEPOT.NAME],
            initialQuantity = this[INVENTORY_UNIT.INITIAL_QUANTITY] ?: 0L,
            quantity = quantity,
            remainingQuantity = quantity - frozenQuantity,
            unitPrice = this[INVENTORY_UNIT.UNIT_PRICE] ?: BigDecimal.ZERO,
            receivedAt = this[INVENTORY_UNIT.RECEIVED_AT]?.toInstant() ?: Instant.EPOCH,
            batchCode = this[INVENTORY_UNIT.BATCH_CODE],
            serialNumber = this[INVENTORY_UNIT.SERIAL_NUMBER],
            status = this[INVENTORY_UNIT.STATUS].name,
        )
    }
}

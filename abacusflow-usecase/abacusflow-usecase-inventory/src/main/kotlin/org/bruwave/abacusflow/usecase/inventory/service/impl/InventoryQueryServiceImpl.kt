package org.bruwave.abacusflow.usecase.inventory.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORIES
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORY_UNIT
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCTS
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDERS
import org.bruwave.abacusflow.generated.jooq.Tables.SALE_ORDERS
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryQueryService
import org.bruwave.abacusflow.usecase.inventory.toTO
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.util.postgres.PGobject
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
@Transactional
class InventoryQueryServiceImpl(
    private val inventoryRepository: InventoryRepository,
    private val dslContext: DSLContext,
    private val objectMapper: ObjectMapper
) : InventoryQueryService {
    override fun listInventories(): List<BasicInventoryTO> {
        val records = dslContext
            .select(
                INVENTORIES.ID,
                INVENTORIES.PRODUCT_ID,
                INVENTORIES.QUANTITY,
                INVENTORIES.SAFETY_STOCK,
                INVENTORIES.MAX_STOCK,
                PRODUCTS.NAME,
                INVENTORY_UNIT.ID,
                INVENTORY_UNIT.UNIT_TYPE,
                INVENTORY_UNIT.PURCHASE_ORDER_ID,
                PURCHASE_ORDERS.NO,
                INVENTORY_UNIT.DEPOT_ID,
                INVENTORY_UNIT.QUANTITY,
                INVENTORY_UNIT.REMAINING_QUANTITY,
                INVENTORY_UNIT.UNIT_PRICE,
                INVENTORY_UNIT.RECEIVED_AT,
                INVENTORY_UNIT.BATCH_CODE,
                INVENTORY_UNIT.SERIAL_NUMBER,
                INVENTORY_UNIT.SALE_ORDER_IDS
            )
            .from(INVENTORIES)
            .leftJoin(INVENTORY_UNIT).on(INVENTORIES.ID.eq(INVENTORY_UNIT.INVENTORY_ID))
            .leftJoin(PRODUCTS).on(INVENTORIES.PRODUCT_ID.eq(PRODUCTS.ID))
            .leftJoin(PURCHASE_ORDERS).on(INVENTORY_UNIT.PURCHASE_ORDER_ID.eq(PURCHASE_ORDERS.ID))
            .fetch()

        // 解析所有 saleOrderIds
        val allSaleOrderIds = records
            .flatMap { parseSaleOrderIdList(it[INVENTORY_UNIT.SALE_ORDER_IDS]) }
            .toSet()

        val saleOrderMap = dslContext
            .select(SALE_ORDERS.ID, SALE_ORDERS.NO)
            .from(SALE_ORDERS)
            .where(SALE_ORDERS.ID.`in`(allSaleOrderIds))
            .fetch()
            .associate { it[SALE_ORDERS.ID]!! to it[SALE_ORDERS.NO]!! }


        return records.groupBy { it[INVENTORIES.ID]!! }.map { (_, group) ->
            val first = group.first()
            BasicInventoryTO(
                id = first[INVENTORIES.ID]!!,
                productName = first[PRODUCTS.NAME] ?: "[未知产品]",
                quantity = first[INVENTORIES.QUANTITY] ?: 0,
                safetyStock = first[INVENTORIES.SAFETY_STOCK],
                maxStock = first[INVENTORIES.MAX_STOCK],
                units = group.mapNotNull { it.toBasicInventoryUnitTO(saleOrderMap) }
            )
        }
    }


    override fun getInventory(id: Long): InventoryTO {
        return inventoryRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Inventory not found with id: $id") }
            .toTO()
    }

    fun Record.toBasicInventoryUnitTO(saleOrderMap: Map<Long, UUID>): BasicInventoryUnitTO? {
        val id = this[INVENTORY_UNIT.ID] ?: return null

        val saleOrderIds = parseSaleOrderIdList(this[INVENTORY_UNIT.SALE_ORDER_IDS])
        val saleOrderNos = saleOrderIds.mapNotNull { saleOrderMap[it] }

        return BasicInventoryUnitTO(
            id = id,
            unitType = this[INVENTORY_UNIT.UNIT_TYPE]!!, // 通常是枚举/字符串，如 "INSTANCE" 或 "BATCH"
            purchaseOrderNo = this[PURCHASE_ORDERS.NO]!!,
            saleOrderNos = saleOrderNos,
            depotId = this[INVENTORY_UNIT.DEPOT_ID],
            quantity = this[INVENTORY_UNIT.QUANTITY] ?: 0L,
            remainingQuantity = this[INVENTORY_UNIT.REMAINING_QUANTITY] ?: 0L,
            unitPrice = this[INVENTORY_UNIT.UNIT_PRICE] ?: 0.0,
            receivedAt = this[INVENTORY_UNIT.RECEIVED_AT]?.toInstant() ?: Instant.EPOCH,
            batchCode = this[INVENTORY_UNIT.BATCH_CODE],
            serialNumber = this[INVENTORY_UNIT.SERIAL_NUMBER]
        )
    }

    fun parseSaleOrderIdList(field: Any?): List<Long> {
        return when (field) {
            is PGobject -> {
                objectMapper.readValue(field.value, object : TypeReference<List<Long>>() {})
            }

            is String -> {
                objectMapper.readValue(field, object : TypeReference<List<Long>>() {})
            }

            else -> emptyList()
        }
    }
}

package org.bruwave.abacusflow.usecase.inventory.service.impl

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.generated.jooq.Tables.DEPOTS
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORIES
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORY_UNIT
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCTS
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDERS
import org.bruwave.abacusflow.generated.jooq.Tables.SALE_ORDERS
import org.bruwave.abacusflow.inventory.InventoryUnit
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryQueryService
import org.bruwave.abacusflow.usecase.inventory.mapper.toTO
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL
import org.jooq.util.postgres.PGobject
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Service
class InventoryQueryServiceImpl(
    private val inventoryRepository: InventoryRepository,
    private val dslContext: DSLContext,
    private val objectMapper: ObjectMapper,
) : InventoryQueryService {
    override fun listInventoriesPage(
        pageable: Pageable,
        productCategoryId: Long?,
        productId: Long?,
        productType: String?,
        depotId: Long?
    ): Page<BasicInventoryTO> {
        val condition = buildList<Condition> {
            productCategoryId?.let {
                add(PRODUCTS.CATEGORY_ID.eq(it))
            }
            productId?.let {
                add(INVENTORIES.PRODUCT_ID.eq(it))
            }
            productType?.let {
                add(PRODUCTS.TYPE.eq(it))
            }
            depotId?.let {
                add(INVENTORY_UNIT.DEPOT_ID.eq(it))
            }
        }

        val records =
            dslContext
                .select(
                    INVENTORIES.ID,
                    INVENTORIES.PRODUCT_ID,
                    INVENTORIES.SAFETY_STOCK,
                    INVENTORIES.MAX_STOCK,
                    PRODUCTS.NAME,
                    PRODUCTS.TYPE,
                    INVENTORY_UNIT.ID,
                    INVENTORY_UNIT.UNIT_TYPE,
                    INVENTORY_UNIT.PURCHASE_ORDER_ID,
                    PURCHASE_ORDERS.NO,
                    DEPOTS.NAME,
                    INVENTORY_UNIT.QUANTITY,
                    INVENTORY_UNIT.REMAINING_QUANTITY,
                    INVENTORY_UNIT.UNIT_PRICE,
                    INVENTORY_UNIT.RECEIVED_AT,
                    INVENTORY_UNIT.BATCH_CODE,
                    INVENTORY_UNIT.SERIAL_NUMBER,
                    DSL.arrayAgg(SALE_ORDERS.NO).`as`("sale_order_nos"),
                )
                .from(INVENTORIES)
                .leftJoin(INVENTORY_UNIT).on(INVENTORIES.ID.eq(INVENTORY_UNIT.INVENTORY_ID))
                .leftJoin(PRODUCTS).on(INVENTORIES.PRODUCT_ID.eq(PRODUCTS.ID))
                .leftJoin(PURCHASE_ORDERS).on(INVENTORY_UNIT.PURCHASE_ORDER_ID.eq(PURCHASE_ORDERS.ID))
                .leftJoin(SALE_ORDERS).on(
                    DSL.condition("{0} = ANY({1})", SALE_ORDERS.ID, INVENTORY_UNIT.SALE_ORDER_IDS),
                )
                .leftJoin(DEPOTS).on(INVENTORY_UNIT.DEPOT_ID.eq(DEPOTS.ID))
                .where(condition)
                .groupBy(
                    INVENTORIES.ID,
                    INVENTORIES.PRODUCT_ID,
                    INVENTORIES.SAFETY_STOCK,
                    INVENTORIES.MAX_STOCK,
                    PRODUCTS.NAME,
                    PRODUCTS.TYPE,
                    INVENTORY_UNIT.ID,
                    INVENTORY_UNIT.UNIT_TYPE,
                    INVENTORY_UNIT.PURCHASE_ORDER_ID,
                    PURCHASE_ORDERS.NO,
                    DEPOTS.NAME,
                    INVENTORY_UNIT.QUANTITY,
                    INVENTORY_UNIT.REMAINING_QUANTITY,
                    INVENTORY_UNIT.UNIT_PRICE,
                    INVENTORY_UNIT.RECEIVED_AT,
                    INVENTORY_UNIT.BATCH_CODE,
                    INVENTORY_UNIT.SERIAL_NUMBER,
                )
                .offset(pageable.offset.toInt())
                .limit(pageable.pageSize)
                .fetch()

        val total = dslContext
            .selectCount()
            .from(INVENTORIES)
            .leftJoin(INVENTORY_UNIT).on(INVENTORIES.ID.eq(INVENTORY_UNIT.INVENTORY_ID))
            .leftJoin(PRODUCTS).on(INVENTORIES.PRODUCT_ID.eq(PRODUCTS.ID))
            .where(condition)
            .fetchOne(0, Int::class.java) ?: 0

        val recordsGrouped = records.groupBy { it[INVENTORIES.ID]!! }.map { (_, group) ->
            val first = group.first()

            val productType: Product.ProductType = Product.ProductType.valueOf(first[PRODUCTS.TYPE]!!)

            BasicInventoryTO(
                id = first[INVENTORIES.ID]!!,
                productName = first[PRODUCTS.NAME] ?: "[未知产品]",
                productType = productType.name,
                quantity = group.mapNotNull { it[INVENTORY_UNIT.REMAINING_QUANTITY] }.sumOf { it },
                depotNames = group.mapNotNull { it[DEPOTS.NAME] },
                safetyStock = first[INVENTORIES.SAFETY_STOCK],
                maxStock = first[INVENTORIES.MAX_STOCK],
                units = group.mapNotNull { it.toBasicInventoryUnitTO() },
            )
        }

        return PageImpl(recordsGrouped, pageable, total.toLong())
    }

    override fun getInventory(id: Long): InventoryTO {
        return inventoryRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Inventory not found with id: $id") }
            .toTO()
    }

    fun Record.toBasicInventoryUnitTO(): BasicInventoryUnitTO? {
        val id = this[INVENTORY_UNIT.ID] ?: return null

        val unitType: InventoryUnit.UnitType = InventoryUnit.UnitType.valueOf(this[INVENTORY_UNIT.UNIT_TYPE]!!)

        val title: String =
            when (unitType) {
                InventoryUnit.UnitType.BATCH -> "${this[PRODUCTS.NAME]}-批次号-${this[INVENTORY_UNIT.BATCH_CODE]}"
                InventoryUnit.UnitType.INSTANCE -> "${this[PRODUCTS.NAME]}-序列号-${this[INVENTORY_UNIT.SERIAL_NUMBER]}"
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
            unitPrice = this[INVENTORY_UNIT.UNIT_PRICE] ?: BigDecimal.ZERO,
            receivedAt = this[INVENTORY_UNIT.RECEIVED_AT]?.toInstant() ?: Instant.EPOCH,
            batchCode = this[INVENTORY_UNIT.BATCH_CODE],
            serialNumber = this[INVENTORY_UNIT.SERIAL_NUMBER],
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

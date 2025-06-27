package org.bruwave.abacusflow.usecase.inventory.service.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.bruwave.abacusflow.db.inventory.InventoryRepository
import org.bruwave.abacusflow.generated.jooq.Tables.DEPOTS
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORIES
import org.bruwave.abacusflow.generated.jooq.Tables.INVENTORY_UNIT
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCTS
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCT_CATEGORIES
import org.bruwave.abacusflow.generated.jooq.Tables.PURCHASE_ORDERS
import org.bruwave.abacusflow.generated.jooq.Tables.SALE_ORDERS
import org.bruwave.abacusflow.inventory.InventoryUnit
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryTO
import org.bruwave.abacusflow.usecase.inventory.BasicInventoryUnitTO
import org.bruwave.abacusflow.usecase.inventory.InventoryTO
import org.bruwave.abacusflow.usecase.inventory.mapper.toTO
import org.bruwave.abacusflow.usecase.inventory.service.InventoryQueryService
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.Record
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

@Service
class InventoryQueryServiceImpl(
    private val inventoryRepository: InventoryRepository,
    private val jooqDsl: DSLContext,
    private val objectMapper: ObjectMapper,
) : InventoryQueryService {
    override fun listInventoriesPage(
        pageable: Pageable,
        productCategoryId: Long?,
        productName: String?,
        productType: String?,
        inventoryUnitCode: String?,
        depotName: String?,
    ): Page<BasicInventoryTO> {
        val condition =
            buildList<Condition> {
                productCategoryId?.let { catId ->
                    val categoryIds = findAllChildrenCategories(catId)
                    if (categoryIds.isNotEmpty()) {
                        add(PRODUCTS.CATEGORY_ID.`in`(categoryIds))
                    } else {
                        add(DSL.noCondition())
                    }
                }
                productName?.takeIf { it.isNotBlank() }?.let {
                    add(PRODUCTS.NAME.containsIgnoreCase(it))
                }
                productType?.let {
                    add(PRODUCTS.TYPE.eq(it))
                }
                inventoryUnitCode?.takeIf { it.isNotBlank() }?.let {
                    val uuidCode =
                        try {
                            UUID.fromString(it) // 尝试将字符串转换为 UUID
                        } catch (e: IllegalArgumentException) {
                            null // 如果转换失败，返回 null
                        }

                    uuidCode?.let { uuid ->
                        add(INVENTORY_UNIT.SERIAL_NUMBER.eq(it).or(INVENTORY_UNIT.BATCH_CODE.eq(uuid)))
                    } ?: run {
                        add(INVENTORY_UNIT.SERIAL_NUMBER.eq(it))
                    }
                }
                depotName?.takeIf { it.isNotBlank() }?.let {
                    add(DEPOTS.NAME.containsIgnoreCase(it))
                }
            }

        val total =
            jooqDsl
                .selectCount()
                .from(INVENTORIES)
                .leftJoin(INVENTORY_UNIT).on(INVENTORIES.ID.eq(INVENTORY_UNIT.INVENTORY_ID))
                .leftJoin(PRODUCTS).on(INVENTORIES.PRODUCT_ID.eq(PRODUCTS.ID))
                .where(condition)
                .fetchOne(0, Int::class.java) ?: 0

        val records =
            jooqDsl
                .select(
                    INVENTORIES.ID,
                    INVENTORIES.PRODUCT_ID,
                    INVENTORIES.SAFETY_STOCK,
                    INVENTORIES.MAX_STOCK,
                    PRODUCTS.NAME,
                    PRODUCTS.SPECIFICATION,
                    PRODUCTS.NOTE,
                    PRODUCTS.TYPE,
                    INVENTORY_UNIT.ID,
                    INVENTORY_UNIT.UNIT_TYPE,
                    INVENTORY_UNIT.PURCHASE_ORDER_ID,
                    PURCHASE_ORDERS.NO,
                    DEPOTS.NAME,
                    INVENTORY_UNIT.INITIAL_QUANTITY,
                    INVENTORY_UNIT.QUANTITY,
                    INVENTORY_UNIT.FROZEN_QUANTITY,
                    INVENTORY_UNIT.UNIT_PRICE,
                    INVENTORY_UNIT.RECEIVED_AT,
                    INVENTORY_UNIT.BATCH_CODE,
                    INVENTORY_UNIT.SERIAL_NUMBER,
                    INVENTORY_UNIT.STATUS,
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
                    PRODUCTS.SPECIFICATION,
                    PRODUCTS.NOTE,
                    PRODUCTS.TYPE,
                    INVENTORY_UNIT.ID,
                    INVENTORY_UNIT.UNIT_TYPE,
                    INVENTORY_UNIT.PURCHASE_ORDER_ID,
                    PURCHASE_ORDERS.NO,
                    DEPOTS.NAME,
                    INVENTORY_UNIT.INITIAL_QUANTITY,
                    INVENTORY_UNIT.QUANTITY,
                    INVENTORY_UNIT.FROZEN_QUANTITY,
                    INVENTORY_UNIT.UNIT_PRICE,
                    INVENTORY_UNIT.RECEIVED_AT,
                    INVENTORY_UNIT.BATCH_CODE,
                    INVENTORY_UNIT.SERIAL_NUMBER,
                    INVENTORY_UNIT.STATUS,
                )
                .orderBy(
                    // 排序将数量计数为 0 的记录推送到末尾
                    DSL.`when`(DSL.count(INVENTORY_UNIT.QUANTITY).eq(0), 1).otherwise(0)
                        .asc(),
                    INVENTORIES.CREATED_AT.desc(),
                )
                .offset(pageable.offset.toInt())
                .limit(pageable.pageSize)
                .fetch()

        val recordsGrouped =
            records.groupBy { it[INVENTORIES.ID]!! }.map { (_, group) ->
                val first = group.first()

                val productType: Product.ProductType = Product.ProductType.valueOf(first[PRODUCTS.TYPE]!!)

                val quantity = group.mapNotNull { it[INVENTORY_UNIT.QUANTITY] }.sumOf { it }
                val frozenQuantity = group.mapNotNull { it[INVENTORY_UNIT.FROZEN_QUANTITY] }.sumOf { it }

                BasicInventoryTO(
                    id = first[INVENTORIES.ID]!!,
                    productName = first[PRODUCTS.NAME]!!,
                    productSpecification = first[PRODUCTS.SPECIFICATION],
                    productNote = first[PRODUCTS.NOTE],
                    productType = productType.name,
                    quantity = group.mapNotNull { it[INVENTORY_UNIT.QUANTITY] }.sumOf { it },
                    initialQuantity = group.mapNotNull { it[INVENTORY_UNIT.INITIAL_QUANTITY] }.sumOf { it },
                    remainingQuantity = quantity - frozenQuantity,
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

    private fun findAllChildrenCategories(categoryId: Long): List<Long> {
        val result = mutableSetOf<Long>()
        val queue = mutableListOf(categoryId)

        while (queue.isNotEmpty()) {
            val currentId = queue.removeAt(0)
            result.add(currentId)

            // 查询直接子分类
            val children =
                jooqDsl
                    .select(PRODUCT_CATEGORIES.ID)
                    .from(PRODUCT_CATEGORIES)
                    .where(PRODUCT_CATEGORIES.PARENT_ID.eq(currentId))
                    .fetch()
                    .map { it.value1() }

            queue.addAll(children)
        }

        return result.toList()
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

        val quantity = this[INVENTORY_UNIT.QUANTITY] ?: 0L
        val frozenQuantity = this[INVENTORY_UNIT.FROZEN_QUANTITY] ?: 0L

        return BasicInventoryUnitTO(
            id = id,
            title = title,
            type = unitType.name, // 通常是枚举/字符串，如 "INSTANCE" 或 "BATCH"
            purchaseOrderNo = this[PURCHASE_ORDERS.NO]!!,
            saleOrderNos = saleOrderNos,
            depotName = this[DEPOTS.NAME],
            initialQuantity = this[INVENTORY_UNIT.INITIAL_QUANTITY] ?: 0L,
            quantity = quantity,
            remainingQuantity = quantity - frozenQuantity,
            unitPrice = this[INVENTORY_UNIT.UNIT_PRICE] ?: BigDecimal.ZERO,
            receivedAt = this[INVENTORY_UNIT.RECEIVED_AT]?.toInstant() ?: Instant.EPOCH,
            batchCode = this[INVENTORY_UNIT.BATCH_CODE],
            serialNumber = this[INVENTORY_UNIT.SERIAL_NUMBER],
            status = this[INVENTORY_UNIT.STATUS],
        )
    }
}

package org.bruwave.abacusflow.usecase.product.service.impl

import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCTS
import org.bruwave.abacusflow.generated.jooq.Tables.PRODUCT_CATEGORIES
import org.bruwave.abacusflow.usecase.product.BasicProductTO
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.bruwave.abacusflow.usecase.product.mapper.toTO
import org.bruwave.abacusflow.usecase.product.service.ProductQueryService
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.impl.DSL
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.stereotype.Service

@Service
class ProductQueryServiceImpl(
    private val productRepository: ProductRepository,
    private val jooqDsl: DSLContext,
) : ProductQueryService {
    override fun getProduct(id: Long): ProductTO =
        productRepository
            .findById(id)
            .orElseThrow { NoSuchElementException("Product not found with id: $id") }
            .toTO()

    override fun getProduct(name: String): ProductTO =
        productRepository
            .findByName(name)
            ?.toTO()
            ?: throw NoSuchElementException("Product not found")

    override fun listProductsPage(
        pageable: Pageable,
        name: String?,
        type: String?,
        enabled: Boolean?,
        categoryId: Long?,
    ): Page<BasicProductTO> {
        val conditions = mutableListOf<Condition>()

        name?.takeIf { it.isNotBlank() }?.let {
            conditions += PRODUCTS.NAME.containsIgnoreCase(it)
        }

        type?.takeIf { it.isNotBlank() }?.let {
            conditions += PRODUCTS.TYPE.eq(it)
        }

        enabled?.let {
            conditions += PRODUCTS.ENABLED.eq(it)
        }

        categoryId?.let { catId ->
            val categoryIds = findAllChildrenCategories(catId)
            if (categoryIds.isNotEmpty()) {
                conditions += PRODUCTS.CATEGORY_ID.`in`(categoryIds)
            } else {
                conditions += DSL.falseCondition()
            }
        }

        // 1. 查询总数
        val total =
            jooqDsl
                .selectCount()
                .from(PRODUCTS)
                .where(conditions)
                .fetchOne(0, Long::class.java) ?: 0L

        // 2. 查询分页数据（只查需要字段）
        val records =
            jooqDsl
                .select(
                    PRODUCTS.ID,
                    PRODUCTS.NAME,
                    PRODUCTS.TYPE,
                    PRODUCTS.ENABLED,
                    PRODUCTS.SPECIFICATION,
                    PRODUCTS.UNIT,
                    PRODUCTS.NOTE,
                    PRODUCTS.CATEGORY_ID,
                    PRODUCT_CATEGORIES.NAME,
                )
                .from(PRODUCTS)
                .leftJoin(PRODUCT_CATEGORIES)
                .on(PRODUCTS.CATEGORY_ID.eq(PRODUCT_CATEGORIES.ID))
                .where(conditions)
                .orderBy(PRODUCTS.CREATED_AT.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize)
                .fetch()
                .map {
                    BasicProductTO(
                        id = it[PRODUCTS.ID]!!,
                        name = it[PRODUCTS.NAME]!!,
                        type = it[PRODUCTS.TYPE],
                        enabled = it[PRODUCTS.ENABLED]!!,
                        specification = it[PRODUCTS.SPECIFICATION],
                        unit = it[PRODUCTS.UNIT],
                        note = it[PRODUCTS.NOTE],
                        categoryName = it[PRODUCT_CATEGORIES.NAME],
                    )
                }

        return PageImpl(records, pageable, total)
    }

    override fun listProducts(): List<ProductTO> {
        return productRepository
            .findAll()
            .map { it.toTO() }
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
}

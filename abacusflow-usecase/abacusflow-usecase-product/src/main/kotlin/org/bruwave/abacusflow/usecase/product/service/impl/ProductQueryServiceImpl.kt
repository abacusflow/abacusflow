package org.bruwave.abacusflow.usecase.product.service.impl

import org.bruwave.abacusflow.db.partner.SupplierRepository
import org.bruwave.abacusflow.db.product.ProductCategoryRepository
import org.bruwave.abacusflow.db.product.ProductInstanceRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.usecase.product.BasicProductTO
import org.bruwave.abacusflow.usecase.product.CreateProductInputTO
import org.bruwave.abacusflow.usecase.product.ProductInstanceForBasicProductTO
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.bruwave.abacusflow.usecase.product.UpdateProductInputTO
import org.bruwave.abacusflow.usecase.product.mapper.mapProductTypeTOToDO
import org.bruwave.abacusflow.usecase.product.mapper.mapProductUnitTOToDO
import org.bruwave.abacusflow.usecase.product.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.product.mapper.toForBasicProductTO
import org.bruwave.abacusflow.usecase.product.mapper.toTO
import org.bruwave.abacusflow.usecase.product.service.ProductCommandService
import org.bruwave.abacusflow.usecase.product.service.ProductQueryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductQueryServiceImpl(
    private val productRepository: ProductRepository,
    private val productInstanceRepository: ProductInstanceRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val supplierRepository: SupplierRepository,
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val saleOrderRepository: SaleOrderRepository,
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

    override fun listProducts(categoryId: Long?): List<BasicProductTO> {
        val products =
            categoryId?.let {
                val ids = getAllSonCategoryIds(categoryId)
                productRepository.findByCategoryIdIn(ids)
            } ?: productRepository.findAll()
        val productInstances = productInstanceRepository.findAll()

        val instancesByProductId: Map<Long, List<ProductInstanceForBasicProductTO>> =
            productInstances
                .map { instance ->
                    instance.toForBasicProductTO()
                }
                .groupBy { it.productId }

        return products.map { product ->
            val instances = instancesByProductId[product.id] ?: emptyList()
            product.toBasicTO(instances)
        }
    }

    private fun getAllSonCategoryIds(categoryId: Long): List<Long> {
        val allCategories = productCategoryRepository.findAll() // 拿到全量分类
        val result = mutableListOf<Long>()

        fun collectChildren(id: Long) {
            result += id
            val children = allCategories.filter { it.parent?.id == id }
            for (child in children) {
                collectChildren(child.id)
            }
        }

        collectChildren(categoryId)
        return result
    }
}

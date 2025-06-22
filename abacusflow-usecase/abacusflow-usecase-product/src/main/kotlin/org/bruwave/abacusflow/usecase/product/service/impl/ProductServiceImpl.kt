package org.bruwave.abacusflow.usecase.product.service.impl

import org.bruwave.abacusflow.db.partner.SupplierRepository
import org.bruwave.abacusflow.db.product.ProductCategoryRepository
import org.bruwave.abacusflow.db.product.ProductInstanceRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.usecase.product.BasicProductInstanceTO
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
import org.bruwave.abacusflow.usecase.product.service.ProductService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productInstanceRepository: ProductInstanceRepository,
    private val productCategoryRepository: ProductCategoryRepository,
    private val supplierRepository: SupplierRepository,
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val saleOrderRepository: SaleOrderRepository,
) : ProductService {
    override fun createProduct(input: CreateProductInputTO): ProductTO {
        val newProductCategory =
            productCategoryRepository.findById(input.categoryId).orElseThrow {
                NoSuchElementException("ProductCategory not found with id: ${input.categoryId}")
            }

        val newProduct =
            Product(
                name = input.name,
                type = mapProductTypeTOToDO(input.type),
                unit = mapProductUnitTOToDO(input.unit),
                unitPrice = input.unitPrice,
                category = newProductCategory,
                supplierId = input.supplierId,
                specification = input.specification,
                note = input.note,
            )
        val product = productRepository.save(newProduct)
        return product.toTO()
    }

    override fun updateProduct(
        id: Long,
        input: UpdateProductInputTO,
    ): ProductTO {
        val product =
            productRepository.findById(id)
                .orElseThrow { NoSuchElementException("Product not found with id: $id") }

        val newProductCategory =
            input.categoryId?.let {
                productCategoryRepository.findById(it)
                    .orElseThrow { NoSuchElementException("ProductCategory not found with id: $id") }
            }

        product.apply {
            updateBasicInfo(
                newName = input.name,
                newNote = input.note,
                newUnitPrice = input.unitPrice,
                newUnit = input.unit?.let { mapProductUnitTOToDO(it) },
                newSpecification = input.specification,
                newCategory = newProductCategory,
            )

            input.categoryId?.let { categoryId ->
                val newProductCategory =
                    productCategoryRepository.findById(categoryId).orElseThrow {
                        NoSuchElementException("Product not found with id: $categoryId")
                    }

                changeCategory(newProductCategory)
            }

            input.supplierId?.let { supplierId -> changeSupplier(supplierId) }
        }

        val updatedProduct = productRepository.saveAndFlush(product)

        return updatedProduct.toTO()
    }

    override fun deleteProduct(id: Long): ProductTO {
        val product =
            productRepository
                .findById(id)
                .orElseThrow { NoSuchElementException("Product not found with id: $id") }

        productRepository.delete(product)

        return product.toTO()
    }

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

        // 批量查依赖实体
        val supplierIds = products.mapNotNull { it.supplierId }.toSet()

        val supplierMap = supplierRepository.findAllById(supplierIds).associateBy { it.id }

        val instancesByProductId: Map<Long, List<ProductInstanceForBasicProductTO>> =
            productInstances
                .map { instance ->
                    instance.toForBasicProductTO()
                }
                .groupBy { it.productId }

        return products.map { product ->
            val supplierName = supplierMap[product.supplierId]?.name ?: "unknown"
            val instances = instancesByProductId[product.id] ?: emptyList()
            product.toBasicTO(supplierName, instances)
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

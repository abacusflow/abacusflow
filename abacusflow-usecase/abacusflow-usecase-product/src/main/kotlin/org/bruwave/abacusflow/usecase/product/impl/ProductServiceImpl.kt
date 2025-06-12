package org.bruwave.abacusflow.usecase.product.impl

import org.bruwave.abacusflow.db.repository.ProductRepository
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.product.ProductCategory
import org.bruwave.abacusflow.usecase.product.ProductService
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductServiceImpl(
    private val productRepository: ProductRepository,
) : ProductService {
    override fun createProduct(product: ProductTO): ProductTO {
        val newProduct = Product(
            name = product.name,
            unitPrice = product.unitPrice,
            category = ProductCategory(name = product.categoryName, code = ""), // TODO: Get category from repository
            supplierId = product.supplierId
        )
        product.specification?.let { newProduct.updateProductBasic(product.name, it, product.unitPrice, newProduct.category) }
        return productRepository.save(newProduct).toProductTO()
    }

    override fun updateProduct(productTO: ProductTO): ProductTO {
        val product = productRepository.findById(productTO.id)
            .orElseThrow { NoSuchElementException("Product not found") }
        product.updateProductBasic(
            newName = productTO.name,
            newSpecification = productTO.specification,
            newPrice = productTO.unitPrice,
            newCategory = ProductCategory(name = productTO.categoryName, code = "") // TODO: Get category from repository
        )
        return productRepository.save(product).toProductTO()
    }

    override fun deleteProduct(productTO: ProductTO): ProductTO {
        val product = productRepository.findById(productTO.id)
            .orElseThrow { NoSuchElementException("Product not found") }
        productRepository.delete(product)
        return productTO
    }

    override fun getProduct(id: Long): ProductTO {
        return productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product not found") }
            .toProductTO()
    }

    override fun getProduct(name: String): ProductTO {
        return productRepository.findByName(name)
            ?.toProductTO()
            ?: throw NoSuchElementException("Product not found")
    }

    override fun listProducts(): List<ProductTO> {
        return productRepository.findAll().map { it.toProductTO() }
    }

    override fun listProductsByCategory(categoryId: Long): List<ProductTO> {
        return productRepository.findByCategoryId(categoryId).map { it.toProductTO() }
    }

    override fun listProductsBySupplier(supplierId: Long): List<ProductTO> {
        return productRepository.findBySupplierId(supplierId).map { it.toProductTO() }
    }

    private fun Product.toProductTO() = ProductTO(
        id = id,
        name = name,
        unitPrice = unitPrice,
        categoryId = category.id,
        categoryName = category.name,
        supplierId = supplierId,
        specification = specification,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
} 
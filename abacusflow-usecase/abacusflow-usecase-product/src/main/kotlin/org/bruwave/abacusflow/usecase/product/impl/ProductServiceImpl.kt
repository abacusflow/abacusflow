package org.bruwave.abacusflow.usecase.product.impl

import org.bruwave.abacusflow.db.product.ProductCategoryRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.usecase.product.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository
) : ProductService {

    override fun createProduct(input: CreateProductInputTO): ProductTO {
        val newProductCategory = productCategoryRepository.findById(input.categoryId).orElseThrow {
            NoSuchElementException("Product not found with id: ${input.categoryId}")
        }

        val newProduct = Product(
            name = input.name,
            specification = input.specification,
            unitPrice = input.unitPrice,
            category = newProductCategory,
            supplierId = input.supplierId
        )
        return productRepository.save(newProduct).toProductTO()
    }

    override fun updateProduct(id: Long, input: UpdateProductInputTO): ProductTO {
        val product = productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product not found with id: $id") }

        val newProductCategory = productCategoryRepository.findById(input.categoryId).orElseThrow {
            NoSuchElementException("Product not found with id: ${input.categoryId}")
        }
        product.updateProductBasic(
            newName = input.name,
            newSpecification = input.specification,
            newPrice = input.unitPrice,
            newCategory = newProductCategory
        )

        return productRepository.save(product).toProductTO()
    }

    override fun deleteProduct(id: Long): ProductTO {
        val product = productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product not found with id: $id") }

        productRepository.delete(product)
        return product.toProductTO()
    }

    override fun getProduct(id: Long): ProductTO {
        return productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product not found with id: $id") }
            .toProductTO()
    }

    override fun getProduct(name: String): ProductTO {
        return productRepository.findByName(name)
            ?.toProductTO()
            ?: throw NoSuchElementException("Product not found")
    }

    override fun listProducts(): List<BasicProductTO> {
        return productRepository.findAll().map { it.toBasicProductTO() }
    }
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

private fun Product.toBasicProductTO() = BasicProductTO(
    id = id,
    name = name,
    categoryName = category.name,
    supplierId = supplierId,
    unitPrice = unitPrice,
    specification = specification
)
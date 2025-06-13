package org.bruwave.abacusflow.usecase.product.impl

import org.bruwave.abacusflow.db.product.ProductCategoryRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.product.ProductUnit
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
            unit = ProductUnit.valueOf(input.unit),
            unitPrice = input.unitPrice,
            category = newProductCategory,
            supplierId = input.supplierId
        )
        return productRepository.save(newProduct).toTO()
    }

    override fun updateProduct(id: Long, input: UpdateProductInputTO): ProductTO {
        val product = productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product not found with id: $id") }

        product.apply {
            updateBasicInfo(
                newName = input.name,
                newSpecification = input.specification,
                newUnitPrice = input.unitPrice,
                newUnit = input.unit?.let { ProductUnit.valueOf(it) },
            )

            input.categoryId?.let { categoryId ->
                val newProductCategory = productCategoryRepository.findById(categoryId).orElseThrow {
                    NoSuchElementException("Product not found with id: ${categoryId}")
                }

                changeCategory(newProductCategory)
            }

            input.supplierId?.let { supplierId -> changeSupplier(supplierId) }
        }

        return productRepository.save(product).toTO()
    }

    override fun deleteProduct(id: Long): ProductTO {
        val product = productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product not found with id: $id") }

        productRepository.delete(product)
        return product.toTO()
    }

    override fun getProduct(id: Long): ProductTO {
        return productRepository.findById(id)
            .orElseThrow { NoSuchElementException("Product not found with id: $id") }
            .toTO()
    }

    override fun getProduct(name: String): ProductTO {
        return productRepository.findByName(name)
            ?.toTO()
            ?: throw NoSuchElementException("Product not found")
    }

    override fun listProducts(): List<BasicProductTO> {
        return productRepository.findAll().map { it.toBasicTO() }
    }
}

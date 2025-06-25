package org.bruwave.abacusflow.usecase.product.service.impl

import org.bruwave.abacusflow.db.partner.SupplierRepository
import org.bruwave.abacusflow.db.product.ProductCategoryRepository
import org.bruwave.abacusflow.db.product.ProductRepository
import org.bruwave.abacusflow.db.transaction.PurchaseOrderRepository
import org.bruwave.abacusflow.db.transaction.SaleOrderRepository
import org.bruwave.abacusflow.product.Product
import org.bruwave.abacusflow.usecase.product.CreateProductInputTO
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.bruwave.abacusflow.usecase.product.UpdateProductInputTO
import org.bruwave.abacusflow.usecase.product.mapper.mapProductTypeTOToDO
import org.bruwave.abacusflow.usecase.product.mapper.mapProductUnitTOToDO
import org.bruwave.abacusflow.usecase.product.mapper.toTO
import org.bruwave.abacusflow.usecase.product.service.ProductCommandService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProductCommandServiceImpl(
    private val productRepository: ProductRepository,
    private val productCategoryRepository: ProductCategoryRepository,
) : ProductCommandService {
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
                category = newProductCategory,
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
}

package org.abacusflow.product.service

import org.abacusflow.product.Product

interface ProductDeletionChecker {
    fun canDelete(product: Product): Boolean
}

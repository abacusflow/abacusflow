package org.bruwave.abacusflow.product.service

import org.bruwave.abacusflow.product.Product

interface ProductDeletionChecker {
    fun canDelete(product: Product): Boolean
}

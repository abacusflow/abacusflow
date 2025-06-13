package org.bruwave.abacusflow.usecase.product

data class BasicProductTO(
    val id: Long,
    val name: String,
    val categoryName: String,
    val supplierId: Long,
    val unitPrice: Double,
    val specification: String?
) 
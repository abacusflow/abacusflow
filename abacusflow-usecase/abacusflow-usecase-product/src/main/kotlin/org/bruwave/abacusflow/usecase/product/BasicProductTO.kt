package org.bruwave.abacusflow.usecase.product

data class BasicProductTO(
    val id: Long,
    val name: String,
    val specification: String?,
    val type: String,
    val categoryName: String,
    val unit: String,
    val note: String?,
    val enabled: Boolean,
    val instances: List<ProductInstanceForBasicProductTO>?,
)

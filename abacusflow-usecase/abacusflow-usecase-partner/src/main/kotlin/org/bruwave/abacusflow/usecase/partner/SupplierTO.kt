package org.bruwave.abacusflow.usecase.partner

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class BasicSupplierTO(
    val id: Long,
    val name: String,
    val contactPerson: String?,
    val phone: String?,
    val address: String?,
    // 历史交易维度
    val totalOrderCount: Int,
    val totalOrderAmount: BigDecimal,
//    val topProductsSummary : List<String>,  // 前10热销商品名（以金额或次数为准）
    val lastOrderDate: LocalDate?,
)

data class SupplierTO(
    val id: Long,
    val name: String,
    val contactPerson: String?,
    val phone: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

package org.abacusflow.usecase.partner

import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate

data class BasicCustomerTO(
    val id: Long,
    val name: String,
    val phone: String?,
    val address: String?,
    // 历史交易维度
    val totalOrderCount: Int,
    val totalOrderAmount: BigDecimal,
//    val topProductsSummary : List<String>,  // 前10热销商品名（以金额或次数为准）
    val lastOrderDate: LocalDate?,
)

data class CustomerTO(
    val id: Long,
    val name: String,
    val phone: String?,
    val address: String?,
    val createdAt: Instant,
    val updatedAt: Instant,
)

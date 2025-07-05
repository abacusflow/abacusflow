package org.bruwave.abacusflow.usecase.partner

import java.math.BigDecimal
import java.time.Instant

data class BasicCustomerTO(
    val id: Long,
    val name: String,
    val phone: String?,
    val address: String?,
    // 历史交易维度
    val totalOrderCount: Int,
    val totalOrderAmount: BigDecimal,
//    val topProductsSummary : List<String>,  // 前10热销商品名（以金额或次数为准）
    val lastOrderTime: Instant?,
)

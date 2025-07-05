package org.bruwave.abacusflow.transaction

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import org.hibernate.annotations.JdbcType
import org.hibernate.dialect.PostgreSQLEnumJdbcType
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "purchase_order_item")
class PurchaseOrderItem(
    val productId: Long,
    // 冗余字段：产品类型（用于区分资产类或普通产品）
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType::class)
    val productType: TransactionProductType,
    @field:Positive
    val quantity: Int = 1,
    @field:PositiveOrZero
    val unitPrice: BigDecimal,
    val serialNumber: String?,
    val batchCode: UUID?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    init {
        when (productType) {
            TransactionProductType.MATERIAL -> {
                // 对于普通产品，productInstanceId 应为空
            }

            TransactionProductType.ASSET -> {
                // 对于资产类，必须只有一个
                require(quantity == 1) { "资产类产品数量只能为 1" }
                requireNotNull(serialNumber) { "资产类产品必须指定 sn" }
            }
        }
    }

    val subtotal: BigDecimal
        get() = unitPrice.multiply(quantity.toBigDecimal())
}

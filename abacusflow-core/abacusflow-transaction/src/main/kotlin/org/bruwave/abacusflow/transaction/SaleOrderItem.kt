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

@Entity
@Table(name = "sale_order_items")
class SaleOrderItem(
    val productId: Long,
    // 冗余字段：产品类型（用于区分资产类或普通商品）
    @Enumerated(EnumType.STRING)
    val productType: TransactionProductType,
    @field:Positive
    val quantity: Int,
    @field:PositiveOrZero
    val unitPrice: Double,
    var productInstanceId: Long?,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    init {
        when (productType) {
            TransactionProductType.MATERIAL -> {
                // 对于普通商品，productInstanceId 应为空
                require(productInstanceId == null) { "普通商品不可关联productInstanceId" }
            }

            TransactionProductType.ASSET -> {
                // 对于资产类，必须只有一个
                require(quantity == 1) { "资产类商品数量只能为 1" }
                requireNotNull(productInstanceId) { "资产类商品必须关联productInstanceId" }
            }
        }
    }

    val subtotal: Double
        get() = unitPrice * quantity
}

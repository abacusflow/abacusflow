package org.bruwave.abacusflow.transaction

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import java.math.BigDecimal

@Entity
@Table(name = "sale_order_items")
class SaleOrderItem(
    val inventoryUnitId: Long,
    // 冗余字段：产品类型（用于区分资产类或普通商品）
    @Enumerated(EnumType.STRING)
    val inventoryUnitType: TransactionInventoryUnitType,
    @field:Positive
    val quantity: Int,
    @field:PositiveOrZero
    val unitPrice: BigDecimal,

    // 默认为 1 表示没有折扣
    @field:DecimalMin("0.0")
    @field:DecimalMax("1.0")
    val discountFactor: BigDecimal = BigDecimal.ONE
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    init {
        when (inventoryUnitType) {
            TransactionInventoryUnitType.BATCH -> {
            }

            TransactionInventoryUnitType.INSTANCE -> {
                // 对于实例类库存单元，一次只能销售一个
                require(quantity == 1) { "资产类商品数量只能为 1" }
            }
        }
    }

    val discountedPrice: BigDecimal
        get() = unitPrice.multiply(discountFactor)
    val subtotal: BigDecimal
        get() = discountedPrice.multiply(quantity.toBigDecimal())
}

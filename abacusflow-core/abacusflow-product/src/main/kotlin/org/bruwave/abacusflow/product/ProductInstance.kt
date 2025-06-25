package org.bruwave.abacusflow.product

class ProductInstance()

// import jakarta.persistence.Entity
// import jakarta.persistence.EnumType
// import jakarta.persistence.Enumerated
// import jakarta.persistence.GeneratedValue
// import jakarta.persistence.GenerationType
// import jakarta.persistence.Id
// import jakarta.persistence.ManyToOne
// import jakarta.persistence.Table
// import jakarta.persistence.UniqueConstraint
// import jakarta.validation.constraints.NotBlank
// import jakarta.validation.constraints.PositiveOrZero
// import org.hibernate.annotations.CreationTimestamp
// import org.hibernate.annotations.UpdateTimestamp
// import org.springframework.data.domain.AbstractAggregateRoot
// import java.math.BigDecimal
// import java.time.Instant
//
// @Entity
// @Table(
//    name = "product_instances",
//    uniqueConstraints = [UniqueConstraint(columnNames = ["serial_number"])],
// )
// class ProductInstance(
//    // 唯一编码，如 SN 编号、序列号
//    val serialNumber: String,
//    @field:PositiveOrZero
//    val unitPrice: BigDecimal,
//    @ManyToOne
//    val product: Product,
//    val purchaseOrderId: Long,
//    saleOrderId: Long? = null,
// ) : AbstractAggregateRoot<ProductInstance>() {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    val id: Long = 0
//
//    @field:NotBlank
//    val name: String = "${product.name}-$serialNumber"
//
//    @Enumerated(EnumType.STRING)
//    var status: ProductInstanceStatus = ProductInstanceStatus.CREATED
//
//    var saleOrderId: Long? = saleOrderId
//        private set
//
//    @CreationTimestamp
//    val createdAt: Instant = Instant.now()
//
//    @UpdateTimestamp
//    var updatedAt: Instant = Instant.now()
//        private set
//
//    fun linkSaleOrder(newSaleOrderId: Long) {
//        saleOrderId = newSaleOrderId
//
//        updatedAt = Instant.now()
//    }
//
//    enum class ProductInstanceStatus {
//        CREATED, // 创建但未入库
//        IN_STOCK, // 已入库
//        SOLD, // 已售出
//    }
// }

package org.bruwave.abacusflow.partner

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.OneToMany
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "customers")
class Customer(
    @field:NotBlank(message = "客户名称不能为空")
    @field:Size(max = 100, message = "客户名称不能超过100字符")
    val name: String,

    @field:Pattern(regexp = "^\\d{11}\$", message = "手机号格式不正确")
    val phone: String? = null,

    @field:Size(max = 200, message = "地址不能超过200字符")
    val address: String? = null
) : AbstractAggregateRoot<Customer>() {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID()

    @CreationTimestamp
    @NotNull
    val createdAt: Instant = Instant.EPOCH

    @UpdateTimestamp
    @NotNull
    var updatedAt: Instant = Instant.EPOCH
        private set

    @OneToMany(mappedBy = "customer")
    val orders: MutableSet<SaleOrder> = mutableSetOf()

    fun updateContactInfo(newPhone: String?, newAddress: String?) {
        phone = newPhone
        address = newAddress
        updatedAt = Instant.now()
    }
}
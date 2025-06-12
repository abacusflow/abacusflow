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
@Table(name = "suppliers")
class Supplier(
    @field:NotBlank(message = "供应商名称不能为空")
    @field:Size(max = 100, message = "供应商名称不能超过100字符")
    val name: String,

    @field:Size(max = 50, message = "联系人不能超过50字符")
    val contactPerson: String? = null,

    @field:Pattern(regexp = "^\\d{11}\$", message = "手机号格式不正确")
    val phone: String? = null
) : AbstractAggregateRoot<Supplier>() {

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

    @OneToMany(mappedBy = "supplier")
    val products: MutableSet<Product> = mutableSetOf()

    @OneToMany(mappedBy = "supplier")
    val orders: MutableSet<PurchaseOrder> = mutableSetOf()

    fun updateContactInfo(newContactPerson: String?, newPhone: String?) {
        contactPerson = newContactPerson
        phone = newPhone
        updatedAt = Instant.now()
    }
}

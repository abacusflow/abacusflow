package org.bruwave.abacusflow.user

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.bruwave.abacusflow.commons.Sex
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.data.domain.AbstractAggregateRoot
import java.time.Instant
import kotlin.random.Random

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["name"]),
    ],
)
class User(
    @field:NotBlank(message = "UserName is required and cannot be blank")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_]*\$",
        message = "User names should contain only letters, numbers and underscores.",
    )
    @field:Size(min = 5, max = 50, message = "Name must be between 1 and 50 characters")
    val name: String,
) : AbstractAggregateRoot<User>() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Enumerated(EnumType.STRING)
    var sex: Sex? = null
        private set

    var age: Int = 0
        private set

    var nick: String = name

    @ManyToMany
    val roles = mutableSetOf<Role>()

    @field:NotNull(message = "Password is required and cannot be blank")
    @field:Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    var password: String = ""
        private set

    @CreationTimestamp
    @NotNull
    val createdAt: Instant = Instant.EPOCH

    @UpdateTimestamp
    @NotNull
    var updatedAt: Instant = Instant.EPOCH
        private set

    var enabled = true
        private set

    var locked = false
        private set

    fun addRole(role: Role) {
        roles.add(role)
        updatedAt = Instant.now()
    }

    fun removeRole(role: Role) {
        roles.remove(role)
        updatedAt = Instant.now()
    }

    fun lock() {
        if (locked) {
            return
        }

        locked = true
        updatedAt = Instant.now()
    }

    fun unlock() {
        if (!locked) {
            return
        }

        locked = false
        updatedAt = Instant.now()
    }

    fun enable() {
        if (enabled) {
            return
        }

        enabled = true
        updatedAt = Instant.now()
    }

    fun disable() {
        if (!enabled) {
            return
        }

        enabled = false
        updatedAt = Instant.now()
    }

    fun updateProfile(
        sex: Sex,
        age: Int,
        nick: String,
    ) {
        this.sex = sex
        this.age = age
        this.nick = nick
        updatedAt = Instant.now()
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
    ) {
        require(enabled) { "User is not enabled" }
        require(!locked) { "User is locked" }

        require(password == oldPassword) { "Old password is incorrect" }

        password = newPassword
        updatedAt = Instant.now()
    }

    fun resetPassword(): String {
        require(enabled) { "User is not enabled" }
        require(!locked) { "User is locked" }

        password = Random(8).toString()
        return password
    }
}

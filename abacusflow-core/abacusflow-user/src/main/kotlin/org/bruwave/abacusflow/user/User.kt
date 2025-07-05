package org.bruwave.abacusflow.user

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
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

@Entity
@Table(
    name = "user_account",
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
    @field:Size(min = 5, max = 50, message = "Name must be between 5 and 50 characters")
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
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")],
    )
    private val rolesMutable: MutableSet<Role> = mutableSetOf()
    val roles: List<Role>
        get() = rolesMutable.toList()

    @field:NotNull(message = "Password is required and cannot be blank")
    var password: String = ""
        private set

    @CreationTimestamp
    @NotNull
    val createdAt: Instant = Instant.now()

    @UpdateTimestamp
    @NotNull
    var updatedAt: Instant = Instant.EPOCH
        private set

    var enabled = true
        private set

    var locked = false
        private set

    fun initPassword(password: String) {
        this.password = password
    }

    fun addRole(role: Role) {
        rolesMutable.add(role)
        updatedAt = Instant.now()
    }

    fun removeRole(role: Role) {
        rolesMutable.remove(role)
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
        newSex: Sex?,
        newAge: Int?,
        newNick: String?,
    ) {
        newSex?.let {
            sex = it
        }
        newAge?.let {
            age = it
        }
        newNick?.let {
            nick = it
        }
        updatedAt = Instant.now()
    }

    fun changePassword(
        oldPassword: String,
        newPassword: String,
        passwordEncoder: UserPasswordEncoder,
    ) {
        require(enabled) { "User is not enabled" }
        require(!locked) { "User is locked" }

        require(oldPassword != newPassword) { "new password does not match cur password" }

        require(passwordEncoder.matches(oldPassword, password)) { "old password is incorrect" }

        password = passwordEncoder.encode(newPassword)
        updatedAt = Instant.now()
    }

    fun resetPassword(passwordEncoder: UserPasswordEncoder): String {
        require(enabled) { "User is not enabled" }
        require(!locked) { "User is locked" }

        val newPassword =
            (1..10)
                .map { chars.random() }
                .joinToString("")
        password = passwordEncoder.encode(newPassword)
        return newPassword
    }

    val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
}

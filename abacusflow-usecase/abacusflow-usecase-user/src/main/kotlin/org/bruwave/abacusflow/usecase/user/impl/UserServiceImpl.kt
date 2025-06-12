package org.bruwave.abacusflow.usecase.user.impl

import org.bruwave.abacusflow.db.repository.UserRepository
import org.bruwave.abacusflow.user.User
import org.bruwave.abacusflow.usecase.user.BasicUserTO
import org.bruwave.abacusflow.usecase.user.UserService
import org.bruwave.abacusflow.usecase.user.UserTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {
    override fun createUser(user: UserTO): UserTO {
        val newUser = User(name = user.name)
        newUser.updateProfile(
            sex = user.sex?.let { org.bruwave.abacusflow.commons.Sex.valueOf(it) },
            age = user.age,
            nick = user.nick
        )
        user.roles.forEach { roleName ->
            // TODO: Add role assignment logic
        }
        return userRepository.save(newUser).toUserTO()
    }

    override fun updateUser(userTO: UserTO): UserTO {
        val user = userRepository.findById(userTO.id).orElseThrow { NoSuchElementException("User not found") }
        user.updateProfile(
            sex = userTO.sex?.let { org.bruwave.abacusflow.commons.Sex.valueOf(it) },
            age = userTO.age,
            nick = userTO.nick
        )
        return userRepository.save(user).toUserTO()
    }

    override fun deleteUser(userTO: UserTO): UserTO {
        val user = userRepository.findById(userTO.id).orElseThrow { NoSuchElementException("User not found") }
        userRepository.delete(user)
        return userTO
    }

    override fun getUser(id: Long): UserTO {
        return userRepository.findById(id)
            .orElseThrow { NoSuchElementException("User not found") }
            .toUserTO()
    }

    override fun getUser(name: String): UserTO {
        return userRepository.findByName(name)
            ?.toUserTO()
            ?: throw NoSuchElementException("User not found")
    }

    override fun listUsers(): List<BasicUserTO> {
        return userRepository.findAll().map { it.toBasicUserTO() }
    }

    private fun User.toUserTO() = UserTO(
        id = id,
        name = name,
        sex = sex?.name,
        age = age,
        nick = nick,
        roles = roles.map { it.name },
        enabled = enabled,
        locked = locked,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    private fun User.toBasicUserTO() = BasicUserTO(
        id = id,
        name = name,
        nick = nick,
        sex = sex?.name,
        age = age,
        roles = roles.map { it.name },
        enabled = enabled,
        locked = locked,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

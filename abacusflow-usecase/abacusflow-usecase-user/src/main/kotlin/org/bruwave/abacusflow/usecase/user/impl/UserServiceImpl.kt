package org.bruwave.abacusflow.usecase.user.impl

import org.bruwave.abacusflow.commons.Sex
import org.bruwave.abacusflow.db.user.UserRepository
import org.bruwave.abacusflow.user.User
import org.bruwave.abacusflow.usecase.user.BasicUserTO
import org.bruwave.abacusflow.usecase.user.CreateUserInputTO
import org.bruwave.abacusflow.usecase.user.UpdateUserInputTO
import org.bruwave.abacusflow.usecase.user.UserService
import org.bruwave.abacusflow.usecase.user.UserTO
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {
    override fun createUser(input: CreateUserInputTO): UserTO {
        val newUser = User(name = input.name)
        newUser.updateProfile(
            newSex = input.sex?.let { Sex.valueOf(it) },
            newAge = input.age,
            newNick = input.nick
        )
        return userRepository.save(newUser).toUserTO()
    }

    override fun updateUser(id: Long, input: UpdateUserInputTO): UserTO {
        val user = userRepository.findById(id).orElseThrow { NoSuchElementException("User not found") }
        user.updateProfile(
            newSex = input.sex?.let { Sex.valueOf(it) },
            newAge = input.age,
            newNick = input.nick
        )
        return userRepository.save(user).toUserTO()
    }

    override fun deleteUser(id: Long): UserTO {
        val user = userRepository.findById(id).orElseThrow { NoSuchElementException("User not found") }
        userRepository.delete(user)
        return user.toUserTO()
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

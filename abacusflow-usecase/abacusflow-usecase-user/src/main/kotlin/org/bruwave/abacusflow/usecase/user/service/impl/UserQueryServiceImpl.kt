package org.bruwave.abacusflow.usecase.user.service.impl

import org.bruwave.abacusflow.db.user.UserRepository
import org.bruwave.abacusflow.usecase.user.BasicUserTO
import org.bruwave.abacusflow.usecase.user.UserTO
import org.bruwave.abacusflow.usecase.user.mapper.toBasicTO
import org.bruwave.abacusflow.usecase.user.mapper.toTO
import org.bruwave.abacusflow.usecase.user.service.UserQueryService
import org.bruwave.abacusflow.user.UserPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserQueryServiceImpl(
    private val userRepository: UserRepository,
    private val userPasswordEncoder: UserPasswordEncoder,
) : UserQueryService {
    override fun getUser(id: Long): UserTO? {
        val user =
            userRepository.findById(id)
                .orElseThrow { NoSuchElementException("User not found") }

        return user.toTO()
    }

    override fun getUser(name: String): UserTO? =
        userRepository
            .findByName(name)
            ?.toTO()
            ?: throw NoSuchElementException("User not found")

    override fun listBasicUsers(): List<BasicUserTO> = userRepository.findAll().map { it.toBasicTO() }
}

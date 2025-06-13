package org.bruwave.abacusflow.portal.web.user

import org.bruwave.abacusflow.portal.web.api.UsersApi
import org.bruwave.abacusflow.portal.web.model.BasicUserVO
import org.bruwave.abacusflow.portal.web.model.CreateUserInputVO
import org.bruwave.abacusflow.portal.web.model.UpdateUserInputVO
import org.bruwave.abacusflow.portal.web.model.UserVO
import org.bruwave.abacusflow.usecase.user.CreateUserInputTO
import org.bruwave.abacusflow.usecase.user.UpdateUserInputTO
import org.bruwave.abacusflow.usecase.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService,
) : UsersApi {
    override fun listUsers(): ResponseEntity<List<BasicUserVO>> {
        val users = userService.listUsers()
        val userVOS =
            users.map { user ->
                BasicUserVO(
                    user.id,
                    user.name,
                    user.nick,
                    user.enabled,
                    user.locked,
                )
            }
        return ResponseEntity.ok(userVOS)
    }

    override fun getUser(id: Long): ResponseEntity<UserVO> {
        val user = userService.getUser(id.toLong())
        return ResponseEntity.ok(
            UserVO(
                user.id,
                user.name,
                user.age,
                user.nick,
                user.roles,
                user.enabled,
                user.locked,
                user.createdAt.toEpochMilli(),
                user.updatedAt.toEpochMilli(),
                user.sex,
            ),
        )
    }

    override fun addUser(createUserInputVO: CreateUserInputVO): ResponseEntity<UserVO> {
        val user = userService.createUser(
            CreateUserInputTO(
                createUserInputVO.name,
                createUserInputVO.nick,
                createUserInputVO.sex,
                createUserInputVO.age,
            )
        )
        return ResponseEntity.ok(
            UserVO(
                user.id,
                user.name,
                user.age,
                user.nick,
                user.roles,
                user.enabled,
                user.locked,
                user.createdAt.toEpochMilli(),
                user.updatedAt.toEpochMilli(),
                user.sex
            ),
        )
    }

    override fun updateUser(id: Long, updateUserInputVO: UpdateUserInputVO): ResponseEntity<UserVO> {
        val user = userService.updateUser(
            id,
            UpdateUserInputTO(
                nick = updateUserInputVO.nick,
                sex = updateUserInputVO.sex,
                age = updateUserInputVO.age,
            ),
        )
        return ResponseEntity.ok(
            UserVO(
                user.id,
                user.name,
                user.age,
                user.nick,
                user.roles,
                user.enabled,
                user.locked,
                user.createdAt.toEpochMilli(),
                user.updatedAt.toEpochMilli(),
                user.sex
            ),
        )
    }

    override fun deleteUser(id: Long): ResponseEntity<UserVO> {
        userService.deleteUser(id)
        return ResponseEntity.ok().build()
    }
}

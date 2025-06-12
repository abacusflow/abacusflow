package org.bruwave.abacusflow.portal.web.user

import org.bruwave.abacusflow.portal.web.api.UsersApi
import org.bruwave.abacusflow.portal.web.model.BasicUserVO
import org.bruwave.abacusflow.portal.web.model.UserVO
import org.bruwave.abacusflow.usecase.user.UserInputTO
import org.bruwave.abacusflow.usecase.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

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
                    user.sex,
                    user.age,
                    user.enabled,
                    user.locked,
                    user.createdAt.toEpochMilli(),
                    user.updatedAt.toEpochMilli()
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
                user.sex,
                user.age,
                user.nick,
                user.roles,
                user.enabled,
                user.locked,
                user.createdAt.toEpochMilli(),
                user.updatedAt.toEpochMilli(),
            ),
        )
    }

    override fun addUser(userVO: UserVO): ResponseEntity<UserVO> {
        return super.addUser(userVO)
    }

    override fun updateUser(id: Long, userVO: UserVO): ResponseEntity<UserVO> {
        val user =
            userService.updateUser(
                UserInputTO(
                    id = id.toLong(),
                    name = userVO.name,
                    nick = userVO.nick,
                    sex = userVO.sex,
                    age = userVO.age,
                    roles = userVO.roles,
                    enabled = userVO.enabled,
                    locked = userVO.locked,
                    createdAt = Instant.ofEpochMilli(userVO.createdAt),
                    updatedAt = Instant.now(),
                ),
            )
        return ResponseEntity.ok(
            UserVO(
                user.id,
                user.name,
                user.nick,
                user.age,
                user.roles,
                user.enabled,
                user.locked,
                user.createdAt.toEpochMilli(),
                user.sex
            ),
        )
    }

    override fun deleteUser(id: Long): ResponseEntity<UserVO> {
        return super.deleteUser(id)
    }
}

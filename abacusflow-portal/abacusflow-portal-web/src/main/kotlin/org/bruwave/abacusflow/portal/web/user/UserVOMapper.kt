package org.bruwave.abacusflow.portal.web.user

import org.bruwave.abacusflow.portal.web.model.BasicProductVO
import org.bruwave.abacusflow.portal.web.model.BasicUserVO
import org.bruwave.abacusflow.portal.web.model.ProductUnitVO
import org.bruwave.abacusflow.portal.web.model.ProductVO
import org.bruwave.abacusflow.portal.web.model.SexVO
import org.bruwave.abacusflow.portal.web.model.SexVO.*
import org.bruwave.abacusflow.portal.web.model.UserVO
import org.bruwave.abacusflow.usecase.product.BasicProductTO
import org.bruwave.abacusflow.usecase.product.ProductTO
import org.bruwave.abacusflow.usecase.user.BasicUserTO
import org.bruwave.abacusflow.usecase.user.UserTO


fun UserTO.toVO(): UserVO = UserVO(
    id,
    name,
    age,
    nick,
    roles,
    enabled,
    locked,
    createdAt.toEpochMilli(),
    updatedAt.toEpochMilli(),
    sex?.let { mapSexTOToVO(it) }
)

fun BasicUserTO.toBasicVO(): BasicUserVO = BasicUserVO(
    id,
    name,
    nick,
    enabled,
    locked,
)

fun SexVO.toTO(): String = when (this) {
    male -> "M"
    female -> "F"
}

private fun mapSexTOToVO(sex: String): SexVO = when (sex.uppercase()) {
    "M" -> SexVO.male
    "F" -> SexVO.female
    else -> throw NoSuchElementException("SexVO $sex not found")
}
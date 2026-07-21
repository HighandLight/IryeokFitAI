package com.parkjunhyung.IryeokFitAi.domain.user.dto

import com.parkjunhyung.IryeokFitAi.domain.user.entity.ENUM.UserRole
import com.parkjunhyung.IryeokFitAi.domain.user.entity.User
import com.parkjunhyung.IryeokFitAi.domain.user.entity.ENUM.UserStatus

data class UserDto(
    val name: String,
    val email: String,
    val phoneNumber: String,
    val status: UserStatus,
    val role: UserRole
)

fun User.toUserDto() = UserDto(
    name = name,
    email = email,
    phoneNumber = phoneNumber,
    status = status,
    role = role
)

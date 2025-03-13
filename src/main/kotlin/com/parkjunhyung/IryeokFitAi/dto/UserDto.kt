package com.parkjunhyung.IryeokFitAi.dto

import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.UserRole
import com.parkjunhyung.IryeokFitAi.repository.entity.User
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.UserStatus

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

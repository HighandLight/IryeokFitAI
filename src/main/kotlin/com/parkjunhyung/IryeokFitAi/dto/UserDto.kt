package com.parkjunhyung.IryeokFitAi.dto

import com.parkjunhyung.IryeokFitAi.repository.entity.User

data class UserDto(val name: String, val email: String, val password: String)

fun User.toUserDto() = UserDto(name = name, email = email, password = password )


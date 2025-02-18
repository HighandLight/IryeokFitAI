package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.dto.UserDto
import com.parkjunhyung.IryeokFitAi.dto.toUserDto
import com.parkjunhyung.IryeokFitAi.request.CreateUserRequest
import com.parkjunhyung.IryeokFitAi.request.toUser
import com.parkjunhyung.IryeokFitAi.service.UserService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController (
    private val userService: UserService
){
    @PostMapping
    fun createUser(@RequestBody createUserRequest: CreateUserRequest): UserDto {
        return userService.createUser(createUserRequest.toUser()).toUserDto()
    }
}
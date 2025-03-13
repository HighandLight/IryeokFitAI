package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.User
import com.parkjunhyung.IryeokFitAi.request.CreateUserRequest
import com.parkjunhyung.IryeokFitAi.request.toUser
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun createUser(createUserRequest: CreateUserRequest): User {
        if (userRepository.findByEmail(createUserRequest.email) != null) {
            throw IllegalArgumentException("이미 존재하는 이메일입니다.")
        }

        val user = createUserRequest.toUser()
        user.password = passwordEncoder.encode(createUserRequest.password)

        return userRepository.save(user)
    }

    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}

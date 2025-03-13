package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.util.PasswordUtil
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository
) {
    fun login(email: String, password: String): Boolean {
        val user = userRepository.findByEmail(email) ?: throw IllegalArgumentException("이메일이 존재하지 않습니다.")

        return PasswordUtil.verifyPassword(password, user.password)
    }
}

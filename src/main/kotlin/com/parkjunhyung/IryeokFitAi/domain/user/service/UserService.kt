package com.parkjunhyung.IryeokFitAi.domain.user.service
import com.parkjunhyung.IryeokFitAi.domain.user.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.domain.user.entity.ENUM.UserRole
import com.parkjunhyung.IryeokFitAi.domain.user.entity.ENUM.UserStatus
import com.parkjunhyung.IryeokFitAi.domain.user.entity.User
import com.parkjunhyung.IryeokFitAi.domain.user.dto.CreateUserRequest
import com.parkjunhyung.IryeokFitAi.domain.auth.request.SigninRequest
import com.parkjunhyung.IryeokFitAi.global.util.JwtUtil
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder,
    private val jwtUtil: JwtUtil
) {
    fun createUser(createUserRequest: CreateUserRequest): User {
        val hashedPassword = passwordEncoder.encode(createUserRequest.password)

        val user = User(
            name = createUserRequest.name,
            email = createUserRequest.email,
            password = hashedPassword,
            phoneNumber = createUserRequest.phoneNumber,
            status = UserStatus.ACTIVATE,
            role = UserRole.USER,
        )

        return userRepository.save(user)
    }

    fun authenticateUser(signinRequest: SigninRequest): String? {
        val user = userRepository.findByEmail(signinRequest.email) ?: return null

        return if (passwordEncoder.matches(signinRequest.password, user.password)) {
            jwtUtil.generateToken(user.email, user.role.name)
        } else {
            null
        }
    }
    fun findByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }
}

package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.User
import org.springframework.stereotype.Service

@Service
class UserService (
    val userRepository: UserRepository
){
    fun createUser(newUser: User): User {
        return userRepository.save(newUser)
    }
    fun findByEmail(email: String)
    = userRepository.findAll().find {
        it.email == email
    }

}

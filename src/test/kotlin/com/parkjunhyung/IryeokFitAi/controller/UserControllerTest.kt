package com.parkjunhyung.IryeokFitAi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.parkjunhyung.IryeokFitAi.service.UserService
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.UserStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.User
import com.parkjunhyung.IryeokFitAi.dto.toUserDto
import com.parkjunhyung.IryeokFitAi.request.CreateUserRequest
import io.mockk.every
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(value = [UserController::class])
@ExtendWith(MockKExtension::class)
class UserControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var userService: UserService

    @Test
    fun `createUser() should create and return the user`() {
        val objectMapper = ObjectMapper()
        val createUserRequest = CreateUserRequest(
            name = "Kyle",
            email = "kyle@gmail.com",
            password = "whenInDoubtChooseChange!",
            phoneNumber = "01012345678"
        )
        val jsonString = objectMapper.writeValueAsString(createUserRequest)

        val mockUser = User(
            name = "Kyle",
            email = "kyle@gmail.com",
            password = "whenInDoubtChooseChange!",
            phoneNumber = "01012345678",
            status = UserStatus.UNVERIFIED
        )

        every { userService.createUser(any()) } returns mockUser 

        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Kyle"))
            .andExpect(jsonPath("$.email").value("kyle@gmail.com"))
            .andExpect(jsonPath("$.phoneNumber").value("01012345678"))
            .andExpect(jsonPath("$.status").value("UNVERIFIED")) // 상태 확인
    }
}

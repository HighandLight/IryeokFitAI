package com.parkjunhyung.IryeokFitAi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.parkjunhyung.IryeokFitAi.service.UserService
import com.parkjunhyung.IryeokFitAi.repository.entity.User
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
        val createUserRequest = CreateUserRequest("Kyle", "kyle@gmail.com", "whenInDoubtChooseChange!")
        val jsonString = objectMapper.writeValueAsString(createUserRequest)

        every { userService.createUser(any()) } returns User(
            name = "kyle",
            email = "kyle@gmail.com",
            password = "whenInDoubtChooseChange!"
        )

        mockMvc
            .perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("kyle"))
            .andExpect(jsonPath("$.email").value("kyle@gmail.com"))
            .andExpect(jsonPath("$.password").value("whenInDoubtChooseChange!"))
    }
}

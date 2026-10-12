package com.parkjunhyung.IryeokFitAi.domain.resume.controller

import com.parkjunhyung.IryeokFitAi.domain.report.service.ReportService
import com.parkjunhyung.IryeokFitAi.global.exception.CustomException
import com.parkjunhyung.IryeokFitAi.global.exception.ErrorCode
import org.springframework.security.test.context.support.WithMockUser
import com.parkjunhyung.IryeokFitAi.global.config.SecurityConfig
import com.parkjunhyung.IryeokFitAi.global.util.JwtUtil
import com.parkjunhyung.IryeokFitAi.domain.user.service.CustomUserDetailsService
import org.springframework.context.annotation.Import

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.parkjunhyung.IryeokFitAi.domain.resume.controller.ResumeController
import com.parkjunhyung.IryeokFitAi.domain.resume.entity.Resume
import com.parkjunhyung.IryeokFitAi.domain.resume.entity.ENUM.ResumeStatus
import com.parkjunhyung.IryeokFitAi.domain.user.entity.User
import com.parkjunhyung.IryeokFitAi.domain.resume.dto.CreateResumeRequest
import com.parkjunhyung.IryeokFitAi.domain.resume.service.ResumeService
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(value = [ResumeController::class])
@ExtendWith(MockKExtension::class)
@Import(SecurityConfig::class)
@WithMockUser
class ResumeControllerTest {
    @MockkBean lateinit var reportService: ReportService

    @MockkBean lateinit var jwtUtil: JwtUtil
    @MockkBean lateinit var customUserDetailsService: CustomUserDetailsService

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var resumeService: ResumeService

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Test
    fun `createResume() should create and return the resume`() {
        val createResumeRequest = CreateResumeRequest(
            userId = 1L,
            originalFilePath = "/uploads/박준형이력서.pdf",
            status = ResumeStatus.UPLOADED
        )

        val jsonString = objectMapper.writeValueAsString(createResumeRequest)

        val mockResume = Resume(
            id = 1L,
            user = User(id = 1L, name = "박준형", email = "parkjunhyung@gmail.com", phoneNumber = "01012345678",password = "bestSoftwareEngineer!"),
            originalFilePath = "/uploads/박준형이력서.pdf",
            status = ResumeStatus.UPLOADED
        )

        every { resumeService.createResume(any()) } returns mockResume

        mockMvc
            .perform(
                post("/resumes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonString)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(mockResume.id))
            .andExpect(jsonPath("$.userId").value(mockResume.user.id))
            .andExpect(jsonPath("$.originalFilePath").value(mockResume.originalFilePath))
            .andExpect(jsonPath("$.imageUrls").isEmpty)
            .andExpect(jsonPath("$.status").value(mockResume.status.toString()))
    }

    @Test
    fun `deleteResume() should return 204 No Content`() {
        every { resumeService.deleteResume(1L) } just runs

        mockMvc.perform(delete("/resumes/1"))
            .andExpect(status().isNoContent())
    }

    @Test
    fun `deleteResume() should return 404 if resume not found`() {
        every { resumeService.deleteResume(any()) } throws CustomException(ErrorCode.RESUME_NOT_FOUND)

        mockMvc.perform(delete("/resumes/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
    }


}

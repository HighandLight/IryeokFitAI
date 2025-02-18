package com.parkjunhyung.IryeokFitAi.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import com.parkjunhyung.IryeokFitAi.request.CreateReportRequest
import com.parkjunhyung.IryeokFitAi.service.ReportService
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

@WebMvcTest(value = [ReportController::class])
@ExtendWith(MockKExtension::class)
class ReportControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc

    @MockkBean
    lateinit var reportService: ReportService

    @Test
    fun `createReport() should create and return the report`() {
        val objectMapper = ObjectMapper()
        val createReportRequest = CreateReportRequest("Kyle")
        val jsonstring = objectMapper.writeValueAsString(createReportRequest)

        every { reportService.createReport(any()) } returns Report(name = "Kyle")

        mockMvc
            .perform(
                post("/reports")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonstring)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Kyle"))
    }
}
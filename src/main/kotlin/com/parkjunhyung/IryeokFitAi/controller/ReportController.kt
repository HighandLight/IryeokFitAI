package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.dto.ReportDto
import com.parkjunhyung.IryeokFitAi.dto.toReportDto
import com.parkjunhyung.IryeokFitAi.request.CreateReportRequest
import com.parkjunhyung.IryeokFitAi.request.toReport
import com.parkjunhyung.IryeokFitAi.service.ReportService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/reports")
class ReportController(
    private val reportService: ReportService
) {
    @PostMapping
    fun createReport(@RequestBody createReportRequest: CreateReportRequest): ReportDto {
        return reportService.createReport(createReportRequest.toReport()).toReportDto()
    }
}


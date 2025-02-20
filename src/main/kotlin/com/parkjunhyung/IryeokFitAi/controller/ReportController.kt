package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.dto.ReportDto
import com.parkjunhyung.IryeokFitAi.dto.toReportDto
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import com.parkjunhyung.IryeokFitAi.request.CreateReportRequest
import com.parkjunhyung.IryeokFitAi.request.toReport
import com.parkjunhyung.IryeokFitAi.service.ReportService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reports")
class ReportController(
    private val reportService: ReportService
) {
    @PostMapping
    fun createReport(@RequestBody report: Report): ResponseEntity<Report> {
        return ResponseEntity.ok(reportService.createReport(report))
    }

    @GetMapping("/user/{userId}")
    fun getReportsByUser(@PathVariable userId: Long): ResponseEntity<List<Report>> {
        return ResponseEntity.ok(reportService.getReportByUser(userId))
    }

    @PatchMapping("/{reportId}/status")
    fun updateReportStatus(@PathVariable reportId: Long, @RequestParam status: ReportStatus): ResponseEntity<Void> {
        reportService.updateReportStatus(reportId, status)
        return ResponseEntity.noContent().build()
    }
}


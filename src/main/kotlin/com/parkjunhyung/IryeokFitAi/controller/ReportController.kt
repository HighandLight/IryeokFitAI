package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.dto.ReportDto
import com.parkjunhyung.IryeokFitAi.dto.toReportDto
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import com.parkjunhyung.IryeokFitAi.request.CreateReportRequest
import com.parkjunhyung.IryeokFitAi.request.UpdateReportRequest
import com.parkjunhyung.IryeokFitAi.service.ReportService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/reports")
class ReportController(
    private val reportService: ReportService
) {
    @PostMapping
    fun createReport(@RequestBody request: CreateReportRequest): ResponseEntity<ReportDto> {
        val report = reportService.createReport(request)
        return ResponseEntity.ok(report.toReportDto())
    }

    @GetMapping("/{reportId}")
    fun getReportById(@PathVariable reportId: Long): ResponseEntity<ReportDto> {
        val report = reportService.getReportById(reportId)
        return ResponseEntity.ok(report.toReportDto())
    }

    @GetMapping("/{reportId}/mark-as-read")
    fun markAsRead(@PathVariable reportId: Long): ResponseEntity<Void> {
        reportService.markAsRead(reportId)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/user/{userId}")
    fun getReportsByUser(@PathVariable userId: Long): ResponseEntity<List<ReportDto>> {
        val reports = reportService.getReportByUser(userId).map { it.toReportDto() }
        return ResponseEntity.ok(reports)
    }

    @PatchMapping("/{reportId}/status")
    fun updateReportStatus(@PathVariable reportId: Long, @RequestParam status: ReportStatus): ResponseEntity<Void> {
        reportService.updateReportStatus(reportId, status)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{reportId}")
    fun updateReport(
        @PathVariable reportId: Long,
        @RequestBody request: UpdateReportRequest
    ): ResponseEntity<ReportDto> {
        val updated = reportService.updateReport(reportId, request)
        return ResponseEntity.ok(updated.toReportDto())
    }

    @DeleteMapping("/{reportId}")
    fun deleteReport(@PathVariable reportId: Long): ResponseEntity<Void> {
        reportService.deleteReport(reportId)
        return ResponseEntity.noContent().build()
    }
}


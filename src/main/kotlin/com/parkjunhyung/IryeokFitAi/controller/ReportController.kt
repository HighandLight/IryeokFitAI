package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.dto.ReportDto
import com.parkjunhyung.IryeokFitAi.dto.toReportDto
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import com.parkjunhyung.IryeokFitAi.request.CreateReportRequest
import com.parkjunhyung.IryeokFitAi.request.UpdateReportRequest
import com.parkjunhyung.IryeokFitAi.service.ReportService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
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
    fun getReportById(
        @PathVariable reportId: Long,
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User

    ): ResponseEntity<ReportDto> {
        val report = reportService.getReportByIdWithCheck(reportId, principal.username)
        return ResponseEntity.ok(report.toReportDto())
    }

    @GetMapping("/{reportId}/mark-as-read")
    fun markAsRead(
        @PathVariable reportId: Long,
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User
    ): ResponseEntity<Void> {
        reportService.markAsRead(reportId, principal.username)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/{reportId}/wait-complete")
    fun waitUntilComplete(
        @PathVariable reportId: Long,
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User
    ): ResponseEntity<ReportDto> {
        val report = reportService.waitUntilCompleted(reportId, principal.username)
        return ResponseEntity.ok(report.toReportDto())
    }

    @GetMapping("/user/{userId}")
    fun getReportsByUser(
        @PathVariable userId: Long,
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User
    ): ResponseEntity<List<ReportDto>> {
        val reports = reportService.getReportByUser(userId, principal.username).map { it.toReportDto() }
        return ResponseEntity.ok(reports)
    }

    @PatchMapping("/{reportId}/status")
    fun updateReportStatus(
        @PathVariable reportId: Long,
        @RequestParam status: ReportStatus,
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User
    ): ResponseEntity<Void> {
        reportService.updateReportStatus(reportId, status, principal.username)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/{reportId}")
    fun updateReport(
        @PathVariable reportId: Long,
        @RequestBody request: UpdateReportRequest,
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User
    ): ResponseEntity<ReportDto> {
        val updated = reportService.updateReport(reportId, request, principal.username)
        return ResponseEntity.ok(updated.toReportDto())
    }

    @DeleteMapping("/{reportId}")
    fun deleteReport(
        @PathVariable reportId: Long,
        @AuthenticationPrincipal principal: org.springframework.security.core.userdetails.User
    ): ResponseEntity<Void> {
        reportService.deleteReport(reportId, principal.username)
        return ResponseEntity.noContent().build()
    }
}


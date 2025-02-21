package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.ReportRepository
import com.parkjunhyung.IryeokFitAi.repository.ResumeRepository
import com.parkjunhyung.IryeokFitAi.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import com.parkjunhyung.IryeokFitAi.request.CreateReportRequest
import io.awspring.cloud.s3.S3Template
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.utils.StringInputStream

@Service
class ReportService(
    private val reportRepository: ReportRepository,
    private val resumeRepository: ResumeRepository,
    private val userRepository: UserRepository
) {
    @Transactional
    fun createReport(request: CreateReportRequest): Report {
        val resume = resumeRepository.findById(request.resumeId)
            .orElseThrow { throw IllegalArgumentException("Resume 없음: ${request.resumeId}") }

        val user = userRepository.findById(request.userId)
            .orElseThrow { throw IllegalArgumentException("User 없음: ${request.userId}") }

        val report = request.toReport(resume, user)
        return reportRepository.save(report)
    }

    fun getReportById(reportId: Long): Report {
        return reportRepository.findById(reportId)
            .orElseThrow { throw IllegalArgumentException("report 없음: report_id : $reportId") }
    }

    fun getReportByUser(userId: Long): List<Report> {
        return reportRepository.findByUserId(userId)
    }

    @Transactional
    fun updateReportStatus(reportId: Long, status: ReportStatus) {
        val report = reportRepository.findById(reportId)
            .orElseThrow{ throw IllegalArgumentException("report 없음: report_id : $reportId")}
        report.status = status
        reportRepository.save(report)
    }

    @Transactional
    fun deleteReport(reportId: Long) {
        val report = reportRepository.findById(reportId)
            .orElseThrow {IllegalArgumentException("report 없음: report_id : $reportId") }
        report.status = ReportStatus.DELETED
        reportRepository.save(report)
    }
}
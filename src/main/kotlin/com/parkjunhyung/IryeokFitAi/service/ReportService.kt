package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.ReportRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import io.awspring.cloud.s3.S3Template
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.utils.StringInputStream

@Service
class ReportService(
    private val reportRepository: ReportRepository
) {
    fun createReport(report: Report): Report {
        return reportRepository.save(report)
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
}
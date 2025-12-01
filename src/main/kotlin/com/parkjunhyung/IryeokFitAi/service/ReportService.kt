package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.ReportRepository
import com.parkjunhyung.IryeokFitAi.repository.ResumeRepository
import com.parkjunhyung.IryeokFitAi.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import com.parkjunhyung.IryeokFitAi.request.CreateReportRequest
import com.parkjunhyung.IryeokFitAi.request.UpdateReportRequest
import io.awspring.cloud.s3.S3Template
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.AccessDeniedException
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
        val resume = request.resumeId?.let { // resume가 null일 수 있도록 바꿈(polling - report 선 생성)
            resumeRepository.findById(it)
                .orElseThrow { throw IllegalArgumentException("Resume 없음: ${request.resumeId}") }
        }

        val user = userRepository.findById(request.userId)
            .orElseThrow { throw IllegalArgumentException("User 없음: ${request.userId}") }

        val report = request.toReport(resume, user)
        return reportRepository.save(report)
    }

    fun getReportById(reportId: Long): Report {
        return reportRepository.findById(reportId)
            .orElseThrow { throw IllegalArgumentException("report 없음: report_id : $reportId") }
    }

    fun getReportByUser(userId: Long, userEmail: String): List<Report> {
        val user = userRepository.findByEmail(userEmail)
            ?: throw AccessDeniedException("사용자 정보를 찾을 수 없습니다.")

        if (user.id != userId) {
            throw AccessDeniedException("다른 사용자의 리포트 목록에는 접근할 수 없습니다.")
        }

        return reportRepository.findByUserId(userId)
            .filter { it.status != ReportStatus.DELETED }
            .sortedByDescending { it.id } // 최신 report 순으로 오도록(내림차순)
    }


    @Transactional
    fun updateReportStatus(reportId: Long, status: ReportStatus, userEmail: String) {
        val report = getReportByIdWithCheck(reportId, userEmail)
        report.status = status
        reportRepository.save(report)
    }

    @Transactional
    fun updateReport(reportId: Long, req: UpdateReportRequest, userEmail: String): Report {
        val report = getReportByIdWithCheck(reportId, userEmail)

        req.title?.let { report.title = it }
        req.jobPostingUrl?.let { report.jobPostingUrl = it }
        req.responsibilities?.let { report.responsibilities = it }
        req.requirements?.let { report.requirements = it }
        req.preferred?.let { report.preferred = it }
        req.skills?.let { report.skills = it }
        req.status?.let { report.status = it }


        req.resumeId?.let {
            val resume = resumeRepository.findById(it)
                .orElseThrow { IllegalArgumentException("Resume not found") }
            report.resume = resume
        }

        return reportRepository.save(report)
    }

    @Transactional
    fun markAsRead(reportId: Long, userEmail: String) { // 피드백 생성 완료 후, 유저가 확인(열람)한 report 구분하기 위함(UX)
        val report = getReportByIdWithCheck(reportId, userEmail)
        if (report.status == ReportStatus.COMPLETED) {
            report.status = ReportStatus.SAVED
            reportRepository.save(report)
        }
    }
    @Transactional
    fun waitUntilCompleted(reportId: Long, userEmail: String, timeoutMs: Long = 30_000, intervalMs: Long = 2000): Report {
        getReportByIdWithCheck(reportId, userEmail)
        val start = System.currentTimeMillis()

        while (System.currentTimeMillis() - start < timeoutMs) {
            val report = getReportById(reportId)
            if (report.status == ReportStatus.COMPLETED) {
                return report
            }
            Thread.sleep(intervalMs)
        }

        return getReportById(reportId) // timeout 후에도 미완료면 현재 상태 반환
    }
    @Transactional
    fun deleteReport(reportId: Long, userEmail: String) {
        val report = getReportByIdWithCheck(reportId, userEmail)
        report.status = ReportStatus.DELETED
        reportRepository.save(report)
    }

    fun getReportByIdWithCheck(reportId: Long, userEmail: String): Report {
        val report = reportRepository.findById(reportId)
            .orElseThrow { NoSuchElementException("해당 리포트를 찾을 수 없습니다. ID: $reportId") }

        if (report.user.email != userEmail) {
            throw AccessDeniedException("이 리포트에 접근할 권한이 없습니다.")
        }

        return report
    }

    @Autowired
    lateinit var reportStatusNotifier: ReportStatusNotifier

    fun markAsCompleted(reportId: Long) {
        val report = getReportById(reportId)
        report.status = ReportStatus.COMPLETED
        reportRepository.save(report)
        reportStatusNotifier.notifyStatusCompleted(report.id)
    }


}
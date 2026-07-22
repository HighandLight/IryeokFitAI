package com.parkjunhyung.IryeokFitAi.domain.report.service

import com.parkjunhyung.IryeokFitAi.domain.report.repository.ReportRepository
import com.parkjunhyung.IryeokFitAi.domain.resume.repository.ResumeRepository
import com.parkjunhyung.IryeokFitAi.domain.user.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.domain.report.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.domain.report.entity.Report
import com.parkjunhyung.IryeokFitAi.domain.report.dto.CreateReportRequest
import com.parkjunhyung.IryeokFitAi.domain.report.dto.UpdateReportRequest
import com.parkjunhyung.IryeokFitAi.global.exception.CustomException
import com.parkjunhyung.IryeokFitAi.global.exception.ErrorCode
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

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
                .orElseThrow { throw CustomException(ErrorCode.RESUME_NOT_FOUND, "resume_id=${request.resumeId}") }
        }

        val user = userRepository.findById(request.userId)
            .orElseThrow { throw CustomException(ErrorCode.USER_NOT_FOUND, "user_id=${request.userId}") }

        val report = request.toReport(resume, user)
        return reportRepository.save(report)
    }

    fun getReportById(reportId: Long): Report {
        return reportRepository.findById(reportId)
            .orElseThrow { throw CustomException(ErrorCode.REPORT_NOT_FOUND, "report_id=$reportId") }
    }

    fun getReportByUser(userId: Long, userEmail: String): List<Report> {
        val user = userRepository.findByEmail(userEmail)
            ?: throw CustomException(ErrorCode.ACCESS_DENIED)

        if (user.id != userId) {
            throw CustomException(ErrorCode.ACCESS_DENIED)
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
                .orElseThrow { CustomException(ErrorCode.RESUME_NOT_FOUND) }
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
            .orElseThrow { CustomException(ErrorCode.REPORT_NOT_FOUND, "report_id=$reportId") }

        if (report.user.email != userEmail) {
            throw CustomException(ErrorCode.ACCESS_DENIED)
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
package com.parkjunhyung.IryeokFitAi.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ReportStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import java.time.LocalDateTime

data class ReportDto(
    val id: Long,
    val resumeId: Long?,
    val userId: Long,
    val title: String,
    val jobPostingUrl: String,
    val status: ReportStatus,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    val createdAt: LocalDateTime,
)

fun Report.toReportDto(): ReportDto {
    return ReportDto(
        id = this.id,
        resumeId = this.resume?.id, // Safecall
        userId = this.user.id,
        title = this.title ?: "제목 없음",
        jobPostingUrl = this.jobPostingUrl ?: "",
        status = this.status,
        createdAt = this.createdAt,
    )
}
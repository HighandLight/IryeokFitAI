package com.parkjunhyung.IryeokFitAi.dto

import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.FeedbackStatus
import com.parkjunhyung.IryeokFitAi.repository.entity.Feedback
import java.time.LocalDateTime

data class FeedbackDto(
    val id: Long,
    val reportId: Long,
    val category: String,
    val priority: String,
    val detailText: String,
    val suggestionText: String,
    val status: FeedbackStatus,
    val createdAt: LocalDateTime
)

fun Feedback.toFeedbackDto(): FeedbackDto {
    return FeedbackDto(
        id = this.id,
        reportId = this.report.id,
        category = this.category,
        priority = this.priority.level,
        detailText = this.detailText,
        suggestionText = this.suggestionText,
        status = this.status,
        createdAt = this.createdAt
    )
}

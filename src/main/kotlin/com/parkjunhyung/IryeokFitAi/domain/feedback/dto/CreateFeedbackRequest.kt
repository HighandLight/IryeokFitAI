package com.parkjunhyung.IryeokFitAi.domain.feedback.dto

import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.ENUM.FeedbackPriority
import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.Feedback
import com.parkjunhyung.IryeokFitAi.domain.report.entity.Report

data class CreateFeedbackRequest (
    val reportId: Long,
    val category: String,
    val priority: FeedbackPriority,
    val detailText: String,
    val suggestionText: String,
)

fun CreateFeedbackRequest.toFeedback(report: Report): Feedback {
    return Feedback (
        report = report,
        category = category,
        priority = priority,
        detailText = detailText,
        suggestionText = suggestionText,
    )
}
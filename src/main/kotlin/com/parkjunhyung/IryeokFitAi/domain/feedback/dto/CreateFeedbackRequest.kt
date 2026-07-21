package com.parkjunhyung.IryeokFitAi.domain.feedback.dto

import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.Feedback
import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.FeedbackPriority
import com.parkjunhyung.IryeokFitAi.domain.report.entity.Report

data class CreateFeedbackRequest (
    val reportId: Long,
    val category: String,
    val priorityId: Long,
    val detailText: String,
    val suggestionText: String,
)

fun CreateFeedbackRequest.toFeedback(
    report: Report,
    priority: FeedbackPriority,
): Feedback {
    return Feedback (
        report = report,
        category = category,
        priority = priority,
        detailText = detailText,
        suggestionText = suggestionText,
    )
}
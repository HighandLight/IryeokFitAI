package com.parkjunhyung.IryeokFitAi.request

import com.parkjunhyung.IryeokFitAi.repository.entity.Feedback
import com.parkjunhyung.IryeokFitAi.repository.entity.FeedbackCategory
import com.parkjunhyung.IryeokFitAi.repository.entity.FeedbackPriority
import com.parkjunhyung.IryeokFitAi.repository.entity.Report

data class CreateFeedbackRequest (
    val reportId: Long,
    val categoryId: Long,
    val priorityId: Long,
    val detailText: String,
    val suggestionText: String,
)

fun CreateFeedbackRequest.toFeedback(
    report: Report,
    category: FeedbackCategory,
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
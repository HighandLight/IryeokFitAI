package com.parkjunhyung.IryeokFitAi.domain.feedback.dto

import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.FeedbackPriority

data class FeedbackPriorityDto(
    val id: Long,
    val level: String
)

fun FeedbackPriority.toFeedbackPriorityDto(): FeedbackPriorityDto {
    return FeedbackPriorityDto(
        id = this.id,
        level = this.level
    )
}

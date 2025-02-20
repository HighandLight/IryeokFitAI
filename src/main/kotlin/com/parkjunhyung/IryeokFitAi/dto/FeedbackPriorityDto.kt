package com.parkjunhyung.IryeokFitAi.dto

import com.parkjunhyung.IryeokFitAi.repository.entity.FeedbackPriority

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

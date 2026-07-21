package com.parkjunhyung.IryeokFitAi.domain.feedback.dto

import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.FeedbackCategory

data class FeedbackCategoryDto(
    val id: Long,
    val name: String
)

fun FeedbackCategory.toFeedbackCategoryDto(): FeedbackCategoryDto {
    return FeedbackCategoryDto(
        id = this.id,
        name = this.name
    )
}

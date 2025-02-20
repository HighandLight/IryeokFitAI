package com.parkjunhyung.IryeokFitAi.dto

import com.parkjunhyung.IryeokFitAi.repository.entity.FeedbackCategory

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

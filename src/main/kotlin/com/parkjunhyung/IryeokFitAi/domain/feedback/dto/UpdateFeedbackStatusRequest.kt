package com.parkjunhyung.IryeokFitAi.domain.feedback.dto

import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.ENUM.FeedbackStatus

data class UpdateFeedbackStatusRequest(
    val status: FeedbackStatus
)

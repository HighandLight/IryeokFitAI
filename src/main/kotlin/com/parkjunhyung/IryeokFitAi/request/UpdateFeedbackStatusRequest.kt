package com.parkjunhyung.IryeokFitAi.request

import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.FeedbackStatus

data class UpdateFeedbackStatusRequest(
    val status: FeedbackStatus
)

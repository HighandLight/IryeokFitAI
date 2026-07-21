package com.parkjunhyung.IryeokFitAi.domain.feedback.service

import com.parkjunhyung.IryeokFitAi.domain.feedback.repository.FeedbackCategoryRepository
import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.FeedbackCategory
import org.springframework.stereotype.Service

@Service
class FeedbackCategoryService (
    private val feedbackCategoryRepository: FeedbackCategoryRepository
) {
    fun getAllCategories(): List<FeedbackCategory> {
        return feedbackCategoryRepository.findAll()
    }
}
package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.FeedbackCategoryRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.FeedbackCategory
import org.springframework.stereotype.Service

@Service
class FeedbackCategoryService (
    private val feedbackCategoryRepository: FeedbackCategoryRepository
) {
    fun getAllCategories(): List<FeedbackCategory> {
        return feedbackCategoryRepository.findAll()
    }
}
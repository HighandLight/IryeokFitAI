package com.parkjunhyung.IryeokFitAi.domain.feedback.controller

import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.FeedbackCategory
import com.parkjunhyung.IryeokFitAi.domain.feedback.service.FeedbackCategoryService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/feedback-categories")
class FeedbackCategoryController (
    private val feedbackCategoryService: FeedbackCategoryService
) {
    @GetMapping
    fun getAllCategories(): ResponseEntity<List<FeedbackCategory>> {
        return ResponseEntity.ok(feedbackCategoryService.getAllCategories())
    }
}
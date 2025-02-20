package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.repository.entity.FeedbackCategory
import com.parkjunhyung.IryeokFitAi.service.FeedbackCategoryService
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
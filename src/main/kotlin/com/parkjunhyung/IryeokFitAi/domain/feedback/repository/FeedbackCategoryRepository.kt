package com.parkjunhyung.IryeokFitAi.domain.feedback.repository

import com.parkjunhyung.IryeokFitAi.domain.feedback.entity.FeedbackCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedbackCategoryRepository: JpaRepository<FeedbackCategory, Long>
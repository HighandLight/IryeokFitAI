package com.parkjunhyung.IryeokFitAi.repository

import com.parkjunhyung.IryeokFitAi.repository.entity.FeedbackCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FeedbackCategoryRepository: JpaRepository<FeedbackCategory, Long>
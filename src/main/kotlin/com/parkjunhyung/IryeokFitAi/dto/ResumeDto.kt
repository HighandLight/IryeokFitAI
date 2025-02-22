package com.parkjunhyung.IryeokFitAi.dto

import com.parkjunhyung.IryeokFitAi.repository.entity.Resume
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ResumeStatus

data class ResumeDto(
    val id: Long,
    val userId: Long,
    val originalFilePath: String,
    val convertedImagePath: String?,
    val resumeText: String?,
    val status: ResumeStatus
)

fun Resume.toResumeDto() = ResumeDto(
    id = id,
    userId = user.id,
    originalFilePath = originalFilePath,
    convertedImagePath = convertedImagePath,
    resumeText = resumeText,
    status = status
)

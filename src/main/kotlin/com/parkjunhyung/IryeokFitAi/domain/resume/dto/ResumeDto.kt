package com.parkjunhyung.IryeokFitAi.domain.resume.dto

import com.parkjunhyung.IryeokFitAi.domain.resume.entity.Resume
import com.parkjunhyung.IryeokFitAi.domain.resume.entity.ENUM.ResumeStatus

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

package com.parkjunhyung.IryeokFitAi.domain.resume.dto

import com.parkjunhyung.IryeokFitAi.domain.resume.entity.Resume
import com.parkjunhyung.IryeokFitAi.domain.user.entity.User
import com.parkjunhyung.IryeokFitAi.domain.resume.entity.ENUM.ResumeStatus

data class CreateResumeRequest(
    val userId: Long,
    val originalFilePath: String,
    val status: ResumeStatus
)

fun CreateResumeRequest.toResume(user: User) = Resume(
    user = user,
    originalFilePath = originalFilePath,
    status = status
)

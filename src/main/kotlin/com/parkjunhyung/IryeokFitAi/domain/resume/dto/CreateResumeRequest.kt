package com.parkjunhyung.IryeokFitAi.domain.resume.dto

import com.parkjunhyung.IryeokFitAi.domain.resume.entity.Resume
import com.parkjunhyung.IryeokFitAi.domain.user.entity.User
import com.parkjunhyung.IryeokFitAi.domain.resume.entity.ENUM.ResumeStatus

data class CreateResumeRequest(
    val userId: Long,
    val originalFilePath: String,
    val convertedImagePath: String? = null, // 변환된 이미지가 없는 경우 대비..(고민 필요)
    val status: ResumeStatus
)

fun CreateResumeRequest.toResume(user: User) = Resume(
    user = user,
    originalFilePath = originalFilePath,
    convertedImagePath = convertedImagePath,
    status = status
)

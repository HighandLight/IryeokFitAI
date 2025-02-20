package com.parkjunhyung.IryeokFitAi.request

import com.parkjunhyung.IryeokFitAi.repository.entity.Resume
import com.parkjunhyung.IryeokFitAi.repository.entity.User
import com.parkjunhyung.IryeokFitAi.repository.entity.ENUM.ResumeStatus

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

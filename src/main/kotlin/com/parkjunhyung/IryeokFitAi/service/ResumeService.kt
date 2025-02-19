package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.ResumeRepository
import com.parkjunhyung.IryeokFitAi.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.Resume
import com.parkjunhyung.IryeokFitAi.repository.entity.ResumeStatus
import com.parkjunhyung.IryeokFitAi.request.CreateResumeRequest
import com.parkjunhyung.IryeokFitAi.request.toResume
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ResumeService (
    val resumeRepository: ResumeRepository,
    private val userRepository: UserRepository,
    private val s3Service: S3Service
){
    fun createResume(request: CreateResumeRequest): Resume {
        val user = userRepository.findById(request.userId)
            .orElseThrow { throw IllegalArgumentException("회원이 존재하지 않습니다! : ${request.userId}") }
        val resume = request.toResume(user)
        return resumeRepository.save(resume)
    }

    fun deleteResume(resumeId: Long) {
        val resume = resumeRepository.findById(resumeId)
            .orElseThrow { throw IllegalArgumentException("이력서를 찾을 수 없습니다: $resumeId") }

        resume.markAsDeleted()
        resumeRepository.save(resume)
    }

    fun uploadResume(userId: Long, file: MultipartFile): Resume {
        val user = userRepository.findById(userId)
            .orElseThrow{ throw IllegalArgumentException("회원이 존재하지 않습니다. : $userId")}
        val fileUrl = s3Service.uploadFile(userId, file)

        val resume = Resume(
            user = user,
            originalFilePath = fileUrl,
            status = ResumeStatus.UPLOADED,
        )

        return resumeRepository.save(resume)
    }
}
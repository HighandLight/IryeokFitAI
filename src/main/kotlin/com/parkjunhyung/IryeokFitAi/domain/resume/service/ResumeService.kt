package com.parkjunhyung.IryeokFitAi.domain.resume.service

import com.parkjunhyung.IryeokFitAi.domain.resume.repository.ResumeRepository
import com.parkjunhyung.IryeokFitAi.domain.user.repository.UserRepository
import com.parkjunhyung.IryeokFitAi.domain.resume.entity.Resume
import com.parkjunhyung.IryeokFitAi.domain.resume.entity.ResumeImage
import com.parkjunhyung.IryeokFitAi.domain.resume.entity.ENUM.ResumeStatus
import com.parkjunhyung.IryeokFitAi.domain.resume.dto.CreateResumeRequest
import com.parkjunhyung.IryeokFitAi.domain.resume.dto.toResume
import com.parkjunhyung.IryeokFitAi.domain.resume.util.PdfUtils.extractTextFromPdf
import com.parkjunhyung.IryeokFitAi.global.exception.CustomException
import com.parkjunhyung.IryeokFitAi.global.exception.ErrorCode
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class ResumeService (
    val resumeRepository: ResumeRepository,
    private val userRepository: UserRepository,
    private val s3Service: S3Service
){
    fun getResumeById(resumeId: Long): Resume {
        return resumeRepository.findById(resumeId)
            .orElseThrow { throw CustomException(ErrorCode.RESUME_NOT_FOUND, "resume_id=$resumeId") }

    }

    fun createResume(request: CreateResumeRequest): Resume {
        val user = userRepository.findById(request.userId)
            .orElseThrow { throw CustomException(ErrorCode.USER_NOT_FOUND, "user_id=${request.userId}") }
        val resume = request.toResume(user)
        return resumeRepository.save(resume)
    }

    fun deleteResume(resumeId: Long) {
        val resume = resumeRepository.findById(resumeId)
            .orElseThrow { throw CustomException(ErrorCode.RESUME_NOT_FOUND, "resume_id=$resumeId") }

        resume.markAsDeleted()
        resumeRepository.save(resume)
    }

fun uploadResume(userId: Long, file: MultipartFile): Resume {
        val user = userRepository.findById(userId)
            .orElseThrow { throw CustomException(ErrorCode.USER_NOT_FOUND, "user_id=$userId") }

        val resumeId = System.currentTimeMillis()
        val pdfUrl = s3Service.uploadPdf(userId, resumeId, file)
        val imageUrls = s3Service.pdfToJpg(userId, resumeId, file)
        val extractedText = extractTextFromPdf(file.inputStream)

        val resume = Resume(
            user = user,
            originalFilePath = pdfUrl,
            resumeText = extractedText,
            status = ResumeStatus.UPLOADED
        )
        imageUrls.forEachIndexed { index, url ->
            resume.images.add(ResumeImage(resume = resume, pageNumber = index + 1, imageUrl = url))
        }

        return resumeRepository.save(resume)
    }
}
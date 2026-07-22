package com.parkjunhyung.IryeokFitAi.domain.resume.controller

import com.parkjunhyung.IryeokFitAi.domain.resume.dto.ResumeDto
import com.parkjunhyung.IryeokFitAi.domain.resume.dto.toResumeDto
import com.parkjunhyung.IryeokFitAi.domain.resume.dto.CreateResumeRequest
import com.parkjunhyung.IryeokFitAi.domain.report.service.ReportService
import com.parkjunhyung.IryeokFitAi.domain.resume.service.ResumeService
import com.parkjunhyung.IryeokFitAi.global.exception.CustomException
import com.parkjunhyung.IryeokFitAi.global.exception.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/resumes")
class ResumeController(
    private val resumeService: ResumeService,
    private val reportService: ReportService
) {

    @GetMapping("/{resumeId}")
    fun getResumeById(@PathVariable resumeId: Long): ResponseEntity<ResumeDto> {
        val resume = resumeService.getResumeById(resumeId)
        return ResponseEntity.ok(resume.toResumeDto())
    }

    @GetMapping("/images/{reportId}")
    fun getResumeImageByReportId(
        @PathVariable reportId: Long,
        @AuthenticationPrincipal principal: User
    ): ResponseEntity<Map<String, List<String>>> {
        val report = reportService.getReportByIdWithCheck(reportId, principal.username)

        val resume = report.resume
            ?: throw CustomException(ErrorCode.REPORT_RESUME_NOT_LINKED)
        val imageUrls = resume.images.sortedBy { it.pageNumber }.map { it.imageUrl }
        val response = mapOf("imageUrls" to imageUrls)
        return ResponseEntity.ok(response)
    }

    @PostMapping
    fun createResume(@RequestBody createResumeRequest: CreateResumeRequest): ResponseEntity<ResumeDto> {
        val resume = resumeService.createResume(createResumeRequest)
        return ResponseEntity.ok(resume.toResumeDto())
    }

    @DeleteMapping("/{resumeId}")
    fun deleteResume(@PathVariable resumeId: Long): ResponseEntity<Void> {
        resumeService.deleteResume(resumeId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/upload")
    fun uploadResume(
        @RequestParam("userId") userId: Long,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ResumeDto> {
        // 파일 비어있는지 체크
        if (file.isEmpty) {
            return ResponseEntity.badRequest().build()
        }

        return try {
            val resume = resumeService.uploadResume(userId, file)
            ResponseEntity.ok(resume.toResumeDto())
        } catch (e: CustomException) {
            // ex, 존재하지 않는 사용자 ID
            ResponseEntity.status(e.errorCode.status).build()
        } catch (e: Exception) {
            // 예기치 않은 서버 오류(S3 업로드 실패 등) - 원인 파악을 위해 반드시 로그 남기기
            e.printStackTrace()
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}

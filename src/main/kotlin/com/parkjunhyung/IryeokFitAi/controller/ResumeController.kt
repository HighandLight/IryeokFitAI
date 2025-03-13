package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.dto.ReportDto
import com.parkjunhyung.IryeokFitAi.dto.ResumeDto
import com.parkjunhyung.IryeokFitAi.dto.toReportDto
import com.parkjunhyung.IryeokFitAi.dto.toResumeDto
import com.parkjunhyung.IryeokFitAi.request.CreateResumeRequest
import com.parkjunhyung.IryeokFitAi.service.ReportService
import com.parkjunhyung.IryeokFitAi.service.ResumeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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
    fun getResumeImageByReportId(@PathVariable reportId: Long): ResponseEntity<Map<String, String?>> {
        val report = reportService.getReportById(reportId)

        val resumeId = report.resume.id
        val resume = resumeService.getResumeById(resumeId)
        val convertedUrl = resume.convertedImagePath
        val response = mapOf("convertedImageUrl" to convertedUrl)
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
        @RequestParam("userId")
        userId: Long,
        @RequestParam("file")
        file: MultipartFile
    ): ResponseEntity<ResumeDto> {
        val resume = resumeService.uploadResume(userId, file)
        return ResponseEntity.ok(resume.toResumeDto())
    }

    @ExceptionHandler(IllegalArgumentException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<String> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.message)
    }// 404 에러 반환토록


}

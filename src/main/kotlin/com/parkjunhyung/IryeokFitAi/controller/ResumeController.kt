package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.dto.ResumeDto
import com.parkjunhyung.IryeokFitAi.dto.toResumeDto
import com.parkjunhyung.IryeokFitAi.request.CreateResumeRequest
import com.parkjunhyung.IryeokFitAi.service.ResumeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/resumes")
class ResumeController(
    private val resumeService: ResumeService
) {
    @PostMapping
    fun createResume(@RequestBody createResumeRequest: CreateResumeRequest): ResponseEntity<ResumeDto> {
        val resume = resumeService.createResume(createResumeRequest)
        return ResponseEntity.ok(resume.toResumeDto())
    }
}

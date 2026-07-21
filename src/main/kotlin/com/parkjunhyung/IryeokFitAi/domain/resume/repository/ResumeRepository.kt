package com.parkjunhyung.IryeokFitAi.domain.resume.repository

import com.parkjunhyung.IryeokFitAi.domain.resume.entity.Resume
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ResumeRepository: JpaRepository<Resume, Long>

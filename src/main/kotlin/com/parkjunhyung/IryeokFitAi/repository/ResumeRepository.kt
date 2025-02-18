package com.parkjunhyung.IryeokFitAi.repository

import com.parkjunhyung.IryeokFitAi.repository.entity.Resume
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ResumeRepository: JpaRepository<Resume, Long>

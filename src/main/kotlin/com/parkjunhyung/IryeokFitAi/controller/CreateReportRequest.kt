package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.repository.entity.Report

data class CreateReportRequest(
    val name: String
)

fun CreateReportRequest.toReport() = Report(name = name)

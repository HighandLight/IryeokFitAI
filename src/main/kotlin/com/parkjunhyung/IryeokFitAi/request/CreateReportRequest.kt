package com.parkjunhyung.IryeokFitAi.request

import com.parkjunhyung.IryeokFitAi.repository.entity.Report

data class CreateReportRequest(
    val name: String
)

fun CreateReportRequest.toReport() = Report(name = name)

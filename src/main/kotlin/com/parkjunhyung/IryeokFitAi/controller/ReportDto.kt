package com.parkjunhyung.IryeokFitAi.controller

import com.parkjunhyung.IryeokFitAi.repository.entity.Report

data class ReportDto(val name: String)

fun Report.toReportDto() = ReportDto(name = name)

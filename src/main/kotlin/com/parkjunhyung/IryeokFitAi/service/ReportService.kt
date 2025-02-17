package com.parkjunhyung.IryeokFitAi.service

import com.parkjunhyung.IryeokFitAi.repository.ReportRepository
import com.parkjunhyung.IryeokFitAi.repository.entity.Report
import io.awspring.cloud.s3.S3Template
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import software.amazon.awssdk.utils.StringInputStream

@Service
class ReportService(
    val reportRepository: ReportRepository,
    val s3Template: S3Template,
    @Value("\${spring.cloud.aws.s3.bucket}")
    val bucketName: String
) {
    fun createReport(newReport: Report): Report {
        s3Template.upload(bucketName, "key2", StringInputStream("value"))
        // P'sA - 001
        return reportRepository.save(newReport)
    }
}
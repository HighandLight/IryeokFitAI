package com.parkjunhyung.IryeokFitAi.service

import io.awspring.cloud.s3.S3Template
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

@Service
class S3Service (
    private val s3Template: S3Template,
    @Value("\${spring.cloud.aws.s3.bucket}")
    private val bucketName: String
) {
    fun uploadFile(userId: Long, file: MultipartFile): String {
        val key = "users/$userId/resumes/${file.originalFilename}"
        val inputStream: InputStream = file.inputStream
        s3Template.upload(bucketName, key, inputStream)
        
        return "https://$bucketName.s3.amazonaws.com/$key"
    }

}
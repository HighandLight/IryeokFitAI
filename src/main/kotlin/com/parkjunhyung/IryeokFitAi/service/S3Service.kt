package com.parkjunhyung.IryeokFitAi.service

import io.awspring.cloud.s3.S3Template
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.rendering.PDFRenderer
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.services.s3.S3Client
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.imageio.ImageIO

@Service
class S3Service (
    private val s3Template: S3Template,
    private val s3Client: S3Client,
    @Value("\${spring.cloud.aws.s3.bucket}")
    private val bucketName: String,
    @Value("\${spring.cloud.aws.s3.directory}")
    private val directory: String
) {
    private fun encodeUserId(userId: Long): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(userId.toString().toByteArray(StandardCharsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    fun uploadPdf(userId: Long, resumeId: Long, file: MultipartFile): String {
        val encodedUserId = encodeUserId(userId)
        val key = "$directory/$encodedUserId/resumes/$resumeId/original.pdf"
        val inputStream: InputStream = file.inputStream

        println("업로드: ${file.originalFilename}, 크기: ${file.size} bytes")

        s3Template.upload(bucketName, key, inputStream)
        return "https://$bucketName.s3.amazonaws.com/$key"
    }

    fun pdfToJpg(userId: Long, resumeId: Long, file: MultipartFile): List<String> {
        val document = PDDocument.load(file.inputStream)
        val renderer = PDFRenderer(document)
        val uploadedImageUrls = mutableListOf<String>()
        val encodedUserId = encodeUserId(userId)

        for (pageIndex in 0 until document.numberOfPages) {
            val image: BufferedImage = renderer.renderImageWithDPI(pageIndex, 150F) // 150 DPI 해상도
            val outputStream = ByteArrayOutputStream()
            ImageIO.write(image, "jpg", outputStream)
            val imageInputStream = ByteArrayInputStream(outputStream.toByteArray())

            val key = "$directory/$encodedUserId/resumes/$resumeId/images/page_${pageIndex + 1}.jpg"
            s3Template.upload(bucketName, key, imageInputStream)

            uploadedImageUrls.add("https://$bucketName.s3.amazonaws.com/$key")
        }

        document.close()
        return uploadedImageUrls
    }

}
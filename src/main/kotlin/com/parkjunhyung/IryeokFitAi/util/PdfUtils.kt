package com.parkjunhyung.IryeokFitAi.util

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import java.io.InputStream

object PdfUtils {
    fun extractTextFromPdf(inputStream: InputStream): String {
        PDDocument.load(inputStream).use { document ->
            val pdfStripper = PDFTextStripper()
            return pdfStripper.getText(document)
        }
    }
}

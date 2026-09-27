package com.parkjunhyung.IryeokFitAi.domain.page.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.beans.factory.annotation.Value
import org.springframework.ui.Model

@Controller
class PageController(
    @Value("\${spring.cloud.aws.s3.cdn-domain}") private val cdnDomain: String
) {

    @GetMapping("/")
    fun showLandingPage(): String {
        return "landing"
    }

    @GetMapping("/signin")
    fun showSignInPage(): String {
        return "signin"
    }

    @GetMapping("/signup")
    fun showSignUpPage(): String {
        return "signup"
    }

    @GetMapping("/resume/new")
    fun showResumeUploadPage(): String {
        return "resume/new"
    }

    @GetMapping("/reports", "/reports/{reportId:[0-9]+}")
    fun showReportsPage(model: Model): String {
        model.addAttribute("resumeImageOrigin", "https://$cdnDomain")
        return "reports/index"
    }

    @GetMapping("/index")
    fun redirectLegacyIndex(): String = "redirect:/resume/new"

    @GetMapping("/report", "/report.html")
    fun redirectLegacyReport(@RequestParam(required = false) reportId: Long?): String {
        return if (reportId != null) "redirect:/reports/$reportId" else "redirect:/reports"
    }
}

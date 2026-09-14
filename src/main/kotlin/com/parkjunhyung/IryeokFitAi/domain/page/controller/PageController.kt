package com.parkjunhyung.IryeokFitAi.domain.page.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.ui.Model

@Controller
class PageController(
    @Value("\${spring.cloud.aws.s3.cdn-domain}") private val cdnDomain: String
) {

    @GetMapping("/")
    fun redirectToIndex(response: HttpServletResponse) {
        response.sendRedirect("/index")
    }

    @GetMapping("/signin")
    fun showSignInPage(): String {
        return "signin"
    }

    @GetMapping("/signup")
    fun showSignUpPage(): String {
        return "signup"
    }

    @GetMapping("/index")
    fun showIndexPage(): String {
        return "index"
    }

    @GetMapping("/report")
    fun showReportPage(model: Model): String {
        model.addAttribute("resumeImageOrigin", "https://$cdnDomain")
        return "report"
    }
}

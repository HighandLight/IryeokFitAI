package com.parkjunhyung.IryeokFitAi.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import jakarta.servlet.http.HttpServletResponse

@Controller
class PageController {

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
    fun showReportPage(): String {
        return "report"
    }
}
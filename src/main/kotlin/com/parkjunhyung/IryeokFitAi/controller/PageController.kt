package com.parkjunhyung.IryeokFitAi.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
@Controller
class PageController {

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
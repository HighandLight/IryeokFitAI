package com.parkjunhyung.IryeokFitAi.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PageController {

    @GetMapping("/signIn.html")
    fun showSignInPage(): String {
        return "signIn"
    }

    @GetMapping("/index.html")
    fun showIndexPage(): String {
        return "index"
    }

    @GetMapping("/report.html")
    fun showReportPage(): String {
        return "report"
    }
}

package com.parkjunhyung.IryeokFitAi.config

import com.parkjunhyung.IryeokFitAi.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil
) : OncePerRequestFilter() {

    @Throws(ServletException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = request.getHeader("Authorization")?.replace("Bearer ", "")

        if (!token.isNullOrBlank() && jwtUtil.validateToken(token)) {
            val email = jwtUtil.extractEmail(token)
            if (!email.isNullOrBlank()) {
                val userDetails: UserDetails = User(email, "", listOf(SimpleGrantedAuthority("ROLE_USER")))

                val authentication = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }

        filterChain.doFilter(request, response)
    }
}

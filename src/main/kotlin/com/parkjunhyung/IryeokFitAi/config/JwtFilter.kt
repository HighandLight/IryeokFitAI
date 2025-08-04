package com.parkjunhyung.IryeokFitAi.config

import com.parkjunhyung.IryeokFitAi.util.JwtUtil
import com.parkjunhyung.IryeokFitAi.service.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

@Component
class JwtFilter(
    private val jwtUtil: JwtUtil,
    private val customUserDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    @Throws(IOException::class, ServletException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader("Authorization")

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            val token = authorizationHeader.substring(7)

            try {
                val email = jwtUtil.extractEmail(token)

                if (email != null && SecurityContextHolder.getContext().authentication == null) {
                    val userDetails = customUserDetailsService.loadUserByUsername(email)

                    if (jwtUtil.validateToken(token)) {
                        val auth = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = auth
                    } else {
                        // 유효하지 않은 토큰
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired JWT token")
                        return
                    }
                }
            } catch (e: Exception) {
                //토큰 파싱 실패
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token format")
                return
            }
        }
        // ㅈㅓㅇ상적인 요청만 다음 필터로 전달되도록
        filterChain.doFilter(request, response)
    }
}

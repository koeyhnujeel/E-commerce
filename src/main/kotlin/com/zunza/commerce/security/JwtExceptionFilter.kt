package com.zunza.commerce.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.util.StandardCharset
import com.zunza.commerce.support.error.ErrorDetails
import com.zunza.commerce.support.response.ApiResponse
import io.jsonwebtoken.ExpiredJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.filter.OncePerRequestFilter

class JwtExceptionFilter(
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: ExpiredJwtException) {
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = MediaType.APPLICATION_JSON.toString()
            response.characterEncoding = StandardCharset.UTF_8.name()

            val errorDetails = ErrorDetails(message = "토큰이 만료되었습니다.")
            val apiResponse = ApiResponse.error(errorDetails)

            objectMapper.writeValue(response.writer, apiResponse)
        }
    }
}

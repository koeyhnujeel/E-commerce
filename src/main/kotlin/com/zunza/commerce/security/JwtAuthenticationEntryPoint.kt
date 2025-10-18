package com.zunza.commerce.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.util.StandardCharset
import com.zunza.commerce.support.error.ErrorDetails
import com.zunza.commerce.support.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        response!!.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON.toString()
        response.characterEncoding = StandardCharset.UTF_8.name()

        val errorDetails = ErrorDetails(message = "로그인이 필요한 작업입니다.")
        val apiResponse = ApiResponse.error(errorDetails)

        objectMapper.writeValue(response.writer, apiResponse)
    }
}

package com.zunza.commerce.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.nimbusds.jose.util.StandardCharset
import com.zunza.commerce.support.error.ErrorDetails
import com.zunza.commerce.support.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

class JwtAccessDeniedHandler(
    private val objectMapper: ObjectMapper
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        accessDeniedException: AccessDeniedException?
    ) {
        response!!.status = HttpStatus.FORBIDDEN.value()
        response.contentType = MediaType.APPLICATION_JSON.toString()
        response.characterEncoding = StandardCharset.UTF_8.name()

        val errorDetails = ErrorDetails(message = "접근 권한이 없습니다.")
        val apiResponse = ApiResponse.error(errorDetails)

        objectMapper.writeValue(response.writer, apiResponse)
    }
}

package com.zunza.commerce.domain.user.controller

import com.zunza.commerce.domain.user.dto.request.LoginRequestDto
import com.zunza.commerce.domain.user.dto.request.SignupRequestDto
import com.zunza.commerce.domain.user.dto.response.LoginResponseDto
import com.zunza.commerce.domain.user.service.AuthService
import com.zunza.commerce.support.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {
    @GetMapping("/duplicate/email")
    fun checkEmailDuplicate(
        @RequestParam email: String
    ): ApiResponse<Any> {
        authService.checkEmailDuplicate(email)
        return ApiResponse.success()
    }

    @GetMapping("/duplicate/nickname")
    fun checkNicknameDuplicate(
        @RequestParam nickname: String
    ): ApiResponse<Any> {
        authService.checkNicknameDuplicate(nickname)
        return ApiResponse.success()
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(
        @Valid @RequestBody request: SignupRequestDto
    ): ApiResponse<Any> {
        authService.signup(request)
        return ApiResponse.success()
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: LoginRequestDto
    ): ResponseEntity<ApiResponse<Any>> {
        val loginResultDto = authService.login(request)
        val loginResponseDto = LoginResponseDto.createFrom(loginResultDto)
        val cookie = generateRefreshTokenCookie(loginResultDto.refreshToken, 7L)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(ApiResponse.success(loginResponseDto))
    }

    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal userId: Long
    ): ResponseEntity<ApiResponse<Any>> {
        authService.logout(userId)
        val cookie = generateRefreshTokenCookie("", 0)
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(ApiResponse.success())
    }

    private fun generateRefreshTokenCookie(value: String, maxAge: Long) =
        ResponseCookie.from("refreshToken", value)
            .httpOnly(true)
            .path("/")
            .maxAge(Duration.ofDays(maxAge))
            .build()
}

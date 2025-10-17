package com.zunza.commerce.domain.user.controller

import com.zunza.commerce.domain.user.dto.request.SignupRequestDto
import com.zunza.commerce.domain.user.service.AuthService
import com.zunza.commerce.support.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

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
}

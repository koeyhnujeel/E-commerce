package com.zunza.commerce.domain.user.controller

import com.zunza.commerce.domain.user.dto.request.SignupRequestDto
import com.zunza.commerce.domain.user.service.UserService
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
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/signup/duplicate/email")
    fun checkEmailDuplicate(
        @RequestParam email: String
    ): ApiResponse<Any> {
        userService.checkEmailDuplicate(email)
        return ApiResponse.success()
    }

    @GetMapping("/signup/duplicate/nickname")
    fun checkNicknameDuplicate(
        @RequestParam nickname: String
    ): ApiResponse<Any> {
        userService.checkNicknameDuplicate(nickname)
        return ApiResponse.success()
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signup(
        @Valid @RequestBody request: SignupRequestDto
    ): ApiResponse<Any> {
        userService.signup(request)
        return ApiResponse.success()
    }
}

package com.zunza.commerce.domain.user.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignupRequestDto(
    @field: NotBlank(message = "이메일을 입력해 주세요.")
    @field: Email(message = "올바른 이메일 형식이 아닙니다.")
    val email: String,

    @field: NotBlank(message = "비밀번호를 입력해 주세요.")
    @field: Size(min = 8, max = 15, message = "비밀번호는 최소 8자 이상 최대 15자 이하여야 합니다.")
    val password: String,

    @field: NotBlank(message = "이름을 입력해 주세요.")
    val name: String,

    @field: NotBlank(message = "닉네임을 입력해 주세요.")
    @field: Size(min = 2, max = 10, message = "닉네임은 최소 2자 이상 최대 10자 이하여야 합니다.")
    val nickname: String,

    @field: NotBlank(message = "전화번호를 입력해 주세요.")
    @field: Pattern(regexp = "^(01[016789]-?\\d{3,4}-?\\d{4}|\\+82-?1[016789]-?\\d{3,4}-?\\d{4})$", message = "올바른 전화번호 형식이 아닙니다.")
    val phone: String
)

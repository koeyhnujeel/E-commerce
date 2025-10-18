package com.zunza.commerce.domain.user.dto.response

import com.zunza.commerce.domain.user.dto.LoginResultDto

data class LoginResponseDto(
    val nickname: String,
    val accessToken: String
) {
    companion object {
        fun createFrom(dto: LoginResultDto) =
            LoginResponseDto(dto.nickname, dto.accessToken)
    }
}


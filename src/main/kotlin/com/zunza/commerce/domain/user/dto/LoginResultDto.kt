package com.zunza.commerce.domain.user.dto

data class LoginResultDto(
    val nickname: String,
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun createOf(nickname: String, accessToken: String, refreshToken: String) =
            LoginResultDto(nickname, accessToken, refreshToken)
    }
}

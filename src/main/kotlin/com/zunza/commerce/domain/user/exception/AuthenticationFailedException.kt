package com.zunza.commerce.domain.user.exception

import com.zunza.commerce.support.error.CustomException
import org.springframework.http.HttpStatus

class AuthenticationFailedException : CustomException(MESSAGE) {
    companion object {
        private const val MESSAGE = "이메일 또는 비밀번호를 확인해 주세요."
    }

    override fun getStatusCode() = HttpStatus.UNAUTHORIZED.value()
}

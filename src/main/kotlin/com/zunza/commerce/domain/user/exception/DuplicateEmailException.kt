package com.zunza.commerce.domain.user.exception

import com.zunza.commerce.support.error.CustomException
import org.springframework.http.HttpStatus

class DuplicateEmailException : CustomException(MESSAGE) {
    companion object {
        private const val MESSAGE = "이미 사용 중인 이메일입니다."
    }

    override fun getStatusCode() = HttpStatus.CONFLICT.value()
}

package com.zunza.commerce.domain.user.exception

import com.zunza.commerce.support.error.CustomException
import org.springframework.http.HttpStatus

class UserNotFoundException : CustomException(MESSAGE) {
    companion object {
        private const val MESSAGE = "사용자를 찾을 수 없습니다."
    }

    override fun getStatusCode() = HttpStatus.NOT_FOUND.value()
}

package com.zunza.commerce.support.advice

import com.zunza.commerce.support.error.CustomException
import com.zunza.commerce.support.error.ErrorDetails
import com.zunza.commerce.support.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiControllerAdvice {

    @ExceptionHandler(CustomException::class)
    fun customExceptionHandler(e: CustomException): ResponseEntity<ApiResponse<Any>> {
        val errorDetails = ErrorDetails(e.message)
        return ResponseEntity.status(e.getStatusCode()).body(ApiResponse.error(errorDetails))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun methodArgumentNotValidExceptionHandler(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Any>> {
        val errorMessages = e.fieldErrors.map { it.defaultMessage }
        val message =  if (errorMessages.count() == 1) errorMessages[0] else null
        val messages = if (errorMessages.count() > 1) errorMessages else null

        val errorDetails = ErrorDetails(message, messages)
        return ResponseEntity.status(e.statusCode).body(ApiResponse.error(errorDetails))
    }
}

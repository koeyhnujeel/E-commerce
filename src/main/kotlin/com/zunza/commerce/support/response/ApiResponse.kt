package com.zunza.commerce.support.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.zunza.commerce.support.error.ErrorDetails

data class ApiResponse<T> private constructor(
    val result: ResultType,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val data: T? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val error: ErrorDetails? = null
) {
    companion object {
        fun success(): ApiResponse<Any> {
            return ApiResponse(ResultType.SUCCESS, null, null)
        }

        fun <S> success(data: S): ApiResponse<S> {
            return ApiResponse(ResultType.SUCCESS, data, null)
        }

        fun error(error: ErrorDetails): ApiResponse<Any> {
            return ApiResponse(ResultType.ERROR, null, error)
        }
    }
}

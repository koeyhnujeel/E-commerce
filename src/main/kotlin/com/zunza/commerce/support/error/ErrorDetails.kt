package com.zunza.commerce.support.error

import com.fasterxml.jackson.annotation.JsonInclude

data class ErrorDetails(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val message: String? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val messages: List<String?>? = null
)

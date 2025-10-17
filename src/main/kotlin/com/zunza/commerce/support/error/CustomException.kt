package com.zunza.commerce.support.error

abstract class CustomException(message: String) : RuntimeException(message) {
    abstract fun getStatusCode(): Int
}

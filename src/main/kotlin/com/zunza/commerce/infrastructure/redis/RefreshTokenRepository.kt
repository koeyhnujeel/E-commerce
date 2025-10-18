package com.zunza.commerce.infrastructure.redis

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class RefreshTokenRepository(
    @Value("\${jwt.refresh-token-expire-time}")
    private val ttl: Long,
    private val stringRedisTemplate: StringRedisTemplate
) {
    companion object {
        private const val REFRESH_TOKEN_KEY_PREFIX = "RT:"
    }
    fun save(userId: Long, refreshToken: String) =
        stringRedisTemplate.opsForValue().set(
            REFRESH_TOKEN_KEY_PREFIX + userId,
            refreshToken,
            ttl,
            TimeUnit.MILLISECONDS)

    fun delete(userId: Long) =
        stringRedisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + userId)
}

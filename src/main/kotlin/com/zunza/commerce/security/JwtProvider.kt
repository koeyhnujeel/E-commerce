package com.zunza.commerce.security

import com.zunza.commerce.domain.user.entity.UserRole
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

private val logger = KotlinLogging.logger { }

@Component
class JwtProvider(
    @Value("\${jwt.secret-key}")
    private val secretKey: String,

    @Value("\${jwt.access-token-expire-time}")
    private val accessTokenExpireTime: Long,

    @Value("\${jwt.refresh-token-expire-time}")
    private val refreshTokenExpireTime: Long
) {
    fun generateAccessToken(userId: Long, userRole: UserRole): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(userId.toString())
            .claim("role", userRole.toString())
            .issuedAt(Date(now.toEpochMilli()))
            .expiration(Date(now.plusMillis(accessTokenExpireTime).toEpochMilli()))
            .signWith(getKey(), Jwts.SIG.HS256)
            .compact()
    }

    fun generateRefreshToken(userId: Long): String {
        val now = Instant.now()
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(Date(now.toEpochMilli()))
            .expiration(Date(now.plusMillis(refreshTokenExpireTime).toEpochMilli()))
            .signWith(getKey(), Jwts.SIG.HS256)
            .compact()
    }

    fun validateToken(token: String): Boolean {
        try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token)
            return true
        } catch (e: ExpiredJwtException) {
            logger.warn { "토큰이 만료되었습니다." }
            throw e
        } catch (e: SignatureException) {
            logger.warn { "토큰 서명이 올바르지 않습니다." }
            return false
        } catch (e: MalformedJwtException) {
            logger.warn { "잘못된 형식의 토큰입니다." }
            return false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = parseClaims(token)
        val userId = claims.subject
        val role = claims["role"] as String

        return UsernamePasswordAuthenticationToken(
            userId.toLong(),
            null,
            listOf(SimpleGrantedAuthority(role))
        )
    }

    private fun getKey(): SecretKey = Keys.hmacShaKeyFor(this.secretKey.toByteArray())

    private fun parseClaims(token: String): Claims {
        return try {
            Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).payload
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }
}

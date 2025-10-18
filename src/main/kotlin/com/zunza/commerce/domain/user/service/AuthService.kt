package com.zunza.commerce.domain.user.service

import com.zunza.commerce.domain.user.dto.LoginResultDto
import com.zunza.commerce.domain.user.dto.request.LoginRequestDto
import com.zunza.commerce.domain.user.exception.AuthenticationFailedException
import com.zunza.commerce.domain.user.repository.UserRepository
import com.zunza.commerce.infrastructure.redis.RefreshTokenRepository
import com.zunza.commerce.security.JwtProvider
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val jwtProvider: JwtProvider,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    fun login(request: LoginRequestDto): LoginResultDto {
        val user = userRepository.findByEmail(request.email)
            ?: throw AuthenticationFailedException()

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw AuthenticationFailedException()
        }

        val accessToken = jwtProvider.generateAccessToken(user.id, user.userRole)
        val refreshToken = jwtProvider.generateRefreshToken(user.id)

        refreshTokenRepository.save(user.id, refreshToken)

        return LoginResultDto.createOf(user.nickname, accessToken, refreshToken)
    }

    fun logout(userId: Long) {
        refreshTokenRepository.delete(userId)
    }
}

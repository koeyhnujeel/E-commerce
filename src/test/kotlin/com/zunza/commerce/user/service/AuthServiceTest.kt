package com.zunza.commerce.user.service

import com.zunza.commerce.domain.user.dto.request.LoginRequestDto
import com.zunza.commerce.domain.user.entity.User
import com.zunza.commerce.domain.user.entity.UserRole
import com.zunza.commerce.domain.user.exception.AuthenticationFailedException
import com.zunza.commerce.domain.user.repository.UserRepository
import com.zunza.commerce.domain.user.service.AuthService
import com.zunza.commerce.infrastructure.redis.RefreshTokenRepository
import com.zunza.commerce.security.JwtProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder

class AuthServiceTest : FunSpec ({
    val jwtProvider = mockk<JwtProvider>()
    val userRepository = mockk<UserRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>()

    val authService = AuthService(
        jwtProvider,
        userRepository,
        passwordEncoder,
        refreshTokenRepository
    )

    afterTest {
        clearAllMocks()
    }

    test("login() 로그인 성공 시, refresh 토큰을 저장하고 닉네임, access, refresh 토큰을 반환한다.") {
        val loginRequestDto = LoginRequestDto("xxx@example.com", "password1!")
        val user = User(email = "aaa@example.com", password = "password1!", name = "김파랑", nickname = "smurf", phone = "010-1234-5678")
        val accessToken = "asdfg"
        val refreshToken = "qwert"

        every { userRepository.findByEmail(any<String>()) } returns user
        every { passwordEncoder.matches(any<String>(), any<String>()) } returns true
        every { jwtProvider.generateAccessToken(any<Long>(), any<UserRole>()) } returns accessToken
        every { jwtProvider.generateRefreshToken(any<Long>()) } returns refreshToken
        justRun { refreshTokenRepository.save(any<Long>(), any<String>()) }

        val result = authService.login(loginRequestDto)

        result.nickname shouldBe user.nickname
        result.accessToken shouldBe accessToken
        result.refreshToken shouldBe refreshToken

        verify(exactly = 1) { userRepository.findByEmail(any<String>()) }
        verify(exactly = 1) { passwordEncoder.matches(any<String>(), any<String>()) }
        verify(exactly = 1) { jwtProvider.generateAccessToken(any<Long>(), any<UserRole>()) }
        verify(exactly = 1) { jwtProvider.generateRefreshToken(any<Long>()) }
        verify(exactly = 1) { refreshTokenRepository.save(any<Long>(), any<String>()) }
    }

    test("login() 존재하지 않는 이메일이면 AuthenticationFailedException이 발생한다.") {
        val loginRequestDto = LoginRequestDto("xxx@example.com", "password1!")

        every { userRepository.findByEmail(any<String>()) } returns null

        shouldThrow<AuthenticationFailedException> {
            authService.login(loginRequestDto)
        }

        verify(exactly = 1) { userRepository.findByEmail(any<String>()) }
    }

    test("login() 비밀번호가 틀리면 AuthenticationFailedException이 발생한다.") {
        val loginRequestDto = LoginRequestDto("xxx@example.com", "password1!")
        val user = User(email = "aaa@example.com", password = "password1!", name = "김파랑", nickname = "smurf", phone = "010-1234-5678")

        every { userRepository.findByEmail(any<String>()) } returns user
        every { passwordEncoder.matches(any<String>(), any<String>()) } returns false

        shouldThrow<AuthenticationFailedException> {
            authService.login(loginRequestDto)
        }

        verify(exactly = 1) { userRepository.findByEmail(any<String>()) }
        verify(exactly = 1) { passwordEncoder.matches(any<String>(), any<String>()) }
    }

    test("logout() 로그아웃 시, refresh 토큰이 삭제된다.") {
        val userId = 1L

        every { refreshTokenRepository.delete(any<Long>()) } returns true

        authService.logout(userId)

        verify(exactly = 1) { refreshTokenRepository.delete(any<Long>()) }
        confirmVerified(refreshTokenRepository)
    }
})

package com.zunza.commerce.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.commerce.config.TestContainersConfig
import com.zunza.commerce.domain.user.dto.request.LoginRequestDto
import com.zunza.commerce.domain.user.entity.User
import com.zunza.commerce.domain.user.entity.UserRole
import com.zunza.commerce.domain.user.repository.UserRepository
import com.zunza.commerce.infrastructure.redis.RefreshTokenRepository
import com.zunza.commerce.security.JwtProvider
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfig::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val jwtProvider: JwtProvider,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val userRepository: UserRepository,
    @Autowired private val passwordEncoder: PasswordEncoder,
    @Autowired private val refreshTokenRepository: RefreshTokenRepository
) : FunSpec({

    beforeSpec {
        val encodedPassword = passwordEncoder.encode("password1!")
        val user = User(email = "qqq@example.com", password = encodedPassword, name = "김파랑", nickname = "smurf", phone = "010-1234-5678")
        userRepository.save(user)
    }

    test("로그인 성공") {
        val loginRequestDto = LoginRequestDto("qqq@example.com", "password1!")

        mockMvc.post("/api/auth/login") {
            content = objectMapper.writeValueAsString(loginRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }
        .andExpect {
            status { isOk() }
            jsonPath("$.result") { value("SUCCESS") }
            jsonPath("$.data.nickname") { value("smurf") }
            jsonPath("$.data.accessToken") { exists() }
            cookie() { exists("refreshToken") }
        }

        refreshTokenRepository.findByUserId(1L) shouldNotBe null
    }

    test("로그인 실패 - 비밀번호 오류") {
        val loginRequestDto = LoginRequestDto("aaa@example.com", "password")

        mockMvc.post("/api/auth/login") {
            content = objectMapper.writeValueAsString(loginRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }
        .andExpect {
            status { isUnauthorized() }
            jsonPath("$.result") { value("ERROR") }
            jsonPath("$.error.message") { value("이메일 또는 비밀번호를 확인해 주세요.") }
        }
    }

    test("로그인 실패 - 존재하지 않는 이메일") {
        val loginRequestDto = LoginRequestDto("xxx@example.com", "password1!")

        mockMvc.post("/api/auth/login") {
            content = objectMapper.writeValueAsString(loginRequestDto)
            contentType = MediaType.APPLICATION_JSON
        }
        .andExpect {
            status { isUnauthorized() }
            jsonPath("$.result") { value("ERROR") }
            jsonPath("$.error.message") { value("이메일 또는 비밀번호를 확인해 주세요.") }
        }
    }

    test("로그아웃") {
        val accessToken = jwtProvider.generateAccessToken(1L, UserRole.ROLE_USER)
        val refreshToken = jwtProvider.generateRefreshToken(1L)
        refreshTokenRepository.save(1L, refreshToken)

        mockMvc.post("/api/auth/logout") {
            contentType = MediaType.APPLICATION_JSON
            header("Authorization", "Bearer $accessToken")
        }
        .andExpect {
            status { isOk() }
            jsonPath("$.result") { value("SUCCESS") }
        }

        refreshTokenRepository.findByUserId(1L) shouldBe null
    }
})

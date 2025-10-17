package com.zunza.commerce.user.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.zunza.commerce.config.TestContainersConfig
import com.zunza.commerce.domain.user.dto.request.SignupRequestDto
import com.zunza.commerce.domain.user.entity.User
import com.zunza.commerce.domain.user.repository.UserRepository
import io.kotest.core.spec.style.FunSpec
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestContainersConfig::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest(
    @Autowired private val mockMvc: MockMvc,
    @Autowired private val objectMapper: ObjectMapper,
    @Autowired private val userRepository: UserRepository
) : FunSpec({

    beforeSpec {
        val user = User(email = "aaa@example.com", password = "password1!", name = "김파랑", nickname = "smurf", phone = "010-1234-5678")
        userRepository.save(user)
    }

    test("이메일 중복 확인 시, 사용 가능한 이메일이면 200을 반환한다.") {
        val email = "xxx@example.com"

        mockMvc.get("/api/auth/duplicate/email") {
            param("email", email)
            contentType = MediaType.APPLICATION_JSON
        }
        .andExpect {
            status { isOk() }
            jsonPath("$.result") { value("SUCCESS") }
        }
    }

    test("이메일 중복 확인 시, 사용 중인 이메일이면 409를 반환한다.") {
        val email = "aaa@example.com"

        mockMvc.get("/api/auth/duplicate/email") {
            param("email", email)
            contentType = MediaType.APPLICATION_JSON
        }
        .andExpect {
            status { isConflict() }
            jsonPath("$.result") { value("ERROR") }
            jsonPath("$.error.message") { value("이미 사용 중인 이메일입니다.") }
        }
    }

    test("닉네임 중복 확인 시, 사용 가능한 닉네임이면 200을 반환한다.") {
        val nickname = "gargamel"

        mockMvc.get("/api/auth/duplicate/nickname") {
            param("nickname", nickname)
            contentType = MediaType.APPLICATION_JSON
        }
        .andExpect {
            status { isOk() }
            jsonPath("$.result") { value("SUCCESS") }
        }
    }

    test("닉네임 중복 확인 시, 사용 중인 닉네임이면 409를 반환한다.") {
        val nickname = "smurf"

        mockMvc.get("/api/auth/duplicate/nickname") {
            param("nickname", nickname)
            contentType = MediaType.APPLICATION_JSON
        }
        .andExpect {
            status { isConflict() }
            jsonPath("$.result") { value("ERROR") }
            jsonPath("$.error.message") { value("이미 사용 중인 닉네임입니다.") }
        }
    }

    test("회원가입 성공 시, 201을 반환한다.") {
        val request = SignupRequestDto("xxx@example.com", "password1!", "김노랑", "gargamel", "010-1234-5679")

        mockMvc.post("/api/auth/signup") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
        }
        .andExpect {
            status { isCreated() }
            jsonPath("$.result") { value("SUCCESS") }
        }
    }
})

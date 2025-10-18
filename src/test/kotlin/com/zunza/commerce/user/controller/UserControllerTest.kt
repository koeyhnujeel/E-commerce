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
class UserControllerTest(
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

        mockMvc.get("/api/users/signup/duplicate/email") {
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

        mockMvc.get("/api/users/signup/duplicate/email") {
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

        mockMvc.get("/api/users/signup/duplicate/nickname") {
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

        mockMvc.get("/api/users/signup/duplicate/nickname") {
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

        mockMvc.post("/api/users/signup") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
        }
        .andExpect {
            status { isCreated() }
            jsonPath("$.result") { value("SUCCESS") }
        }
    }

    test("회원가입 실패, 잘못된 이메일 형식") {
        val request = SignupRequestDto("example.com", "password1!", "김노랑", "gargamel1", "010-1234-3239")

        mockMvc.post("/api/users/signup") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.result") { value("ERROR") }
                jsonPath("$.error.message") { value("올바른 이메일 형식이 아닙니다.") }
            }
    }

    test("회원가입 실패, 잘못된 비밀번호 길이") {
        val request = SignupRequestDto("fff@example.com", "pass", "김노랑", "gargamel2", "010-1234-5659")

        mockMvc.post("/api/users/signup") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.result") { value("ERROR") }
                jsonPath("$.error.message") { value("비밀번호는 최소 8자 이상 최대 15자 이하여야 합니다.") }
            }
    }

    test("회원가입 실패, 잘못된 닉네임 길이") {
        val request = SignupRequestDto("yyy@example.com", "password1!", "김노랑", "gargamelllllll", "010-1223-5679")

        mockMvc.post("/api/users/signup") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.result") { value("ERROR") }
                jsonPath("$.error.message") { value("닉네임은 최소 2자 이상 최대 10자 이하여야 합니다.") }
            }
    }

    test("회원가입 실패, 잘못된 전화번호 형식") {
        val request = SignupRequestDto("zzz@example.com", "password1!", "김노랑", "gargamel3", "01d-1223-5679")

        mockMvc.post("/api/users/signup") {
            content = objectMapper.writeValueAsString(request)
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.result") { value("ERROR") }
                jsonPath("$.error.message") { value("올바른 전화번호 형식이 아닙니다.") }
            }
    }
})

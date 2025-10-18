package com.zunza.commerce.user.service

import com.zunza.commerce.domain.user.dto.request.SignupRequestDto
import com.zunza.commerce.domain.user.entity.User
import com.zunza.commerce.domain.user.entity.UserRole
import com.zunza.commerce.domain.user.entity.UserType
import com.zunza.commerce.domain.user.exception.DuplicateEmailException
import com.zunza.commerce.domain.user.exception.DuplicateNicknameException
import com.zunza.commerce.domain.user.repository.UserRepository
import com.zunza.commerce.domain.user.service.AuthService
import com.zunza.commerce.infrastructure.redis.RefreshTokenRepository
import com.zunza.commerce.security.JwtProvider
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder

class AuthServiceTest : FunSpec ({
    val userRepository = mockk<UserRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val jwtProvider = mockk<JwtProvider>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>()

    val authService = AuthService(
        userRepository = userRepository,
        passwordEncoder = passwordEncoder,
        jwtProvider = jwtProvider,
        refreshTokenRepository = refreshTokenRepository
    )

    afterTest {
        clearMocks(userRepository)
    }

    test("이메일이 중복이 아니라면 예외가 발생하지 않는다.") {
        val email = "xxx@email.com"
        every { userRepository.existsByEmail(email) } returns false

        shouldNotThrow<DuplicateEmailException> {
            authService.checkEmailDuplicate(email)
        }

        verify(exactly = 1) { userRepository.existsByEmail(email) }
    }

    test("이메일이 중복이라면 예외가 발생한다.") {
        val email = "xxx@email.com"
        every { userRepository.existsByEmail(email) } returns true

        shouldThrow<DuplicateEmailException> {
            authService.checkEmailDuplicate(email)
        }

        verify(exactly = 1) { userRepository.existsByEmail(email) }
    }

    test("닉네임이 중복이 아니라면 예외가 발생하지 않는다.") {
        val nickname = "tester"
        every { userRepository.existsByNickname(nickname) } returns false

        shouldNotThrow<DuplicateNicknameException> {
            authService.checkNicknameDuplicate(nickname)
        }

        verify(exactly = 1) { userRepository.existsByNickname(nickname) }
    }

    test("닉네임이 중복이라면 예외가 발생한다.") {
        val nickname = "tester"
        every { userRepository.existsByNickname(nickname) } returns true

        shouldThrow<DuplicateNicknameException> {
            authService.checkNicknameDuplicate(nickname)
        }

        verify(exactly = 1) { userRepository.existsByNickname(nickname) }
    }

    test("signup() 회원가입 시 비밀번호를 암호화하고 저장한다.") {
        val request = SignupRequestDto("xxx@example.com", "password1!", "김블루", "gargamel", "010-1234-5678")
        val encodedPassword = "_encodedPassword_"
        every { passwordEncoder.encode(request.password) } returns encodedPassword

        val userSlot = slot<User>()
        every { userRepository.save(capture(userSlot)) } returns mockk()

        authService.signup(request)

        verify(exactly = 1) { userRepository.save(any<User>()) }

        val savedUser = userSlot.captured
        savedUser.email shouldBe "xxx@example.com"
        savedUser.password shouldBe encodedPassword
        savedUser.name shouldBe "김블루"
        savedUser.nickname shouldBe "gargamel"
        savedUser.phone shouldBe "010-1234-5678"
        savedUser.userRole shouldBe UserRole.ROLE_USER
        savedUser.userType shouldBe UserType.NORMAL
        savedUser.oAuth2Provider shouldBe null
    }
})

package com.zunza.commerce.domain.user.service

import com.zunza.commerce.domain.user.dto.request.SignupRequestDto
import com.zunza.commerce.domain.user.entity.User
import com.zunza.commerce.domain.user.exception.DuplicateEmailException
import com.zunza.commerce.domain.user.exception.DuplicateNicknameException
import com.zunza.commerce.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {
    fun checkEmailDuplicate(email: String) {
        if (userRepository.existsByEmail(email)) {
            throw DuplicateEmailException()
        }
    }

    fun checkNicknameDuplicate(nickname: String) {
        if (userRepository.existsByNickname(nickname)) {
            throw DuplicateNicknameException()
        }
    }

    fun signup(request: SignupRequestDto) {
        val encodedPassword = passwordEncoder.encode(request.password)
        val user = User.createNormalUser(
            request.email,
            encodedPassword,
            request.name,
            request.nickname,
            request.phone
        )

        userRepository.save(user)
    }
}

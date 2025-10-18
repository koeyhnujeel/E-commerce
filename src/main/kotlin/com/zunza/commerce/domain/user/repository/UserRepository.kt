package com.zunza.commerce.domain.user.repository

import com.zunza.commerce.domain.user.entity.User
import com.zunza.commerce.domain.user.exception.UserNotFoundException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

fun UserRepository.findByIdOrThrow(id: Long) =
    this.findByIdOrNull(id) ?: throw UserNotFoundException()

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByEmail(email: String): Boolean
    fun existsByNickname(nickname: String): Boolean
    fun findByEmail(email: String): User?
}

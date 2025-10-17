package com.zunza.commerce.domain.user.repository

import com.zunza.commerce.domain.user.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun existsByEmail(email: String): Boolean
    fun existsByNickname(nickname: String): Boolean
}

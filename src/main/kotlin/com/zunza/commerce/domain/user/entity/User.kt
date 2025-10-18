package com.zunza.commerce.domain.user.entity

import com.zunza.commerce.support.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val email: String = "",

    @Column(nullable = false)
    var password: String? = null,

    @Column(nullable = false)
    val name: String = "",

    @Column(nullable = false, unique = true)
    var nickname: String = "",

    @Column(nullable = false, unique = true)
    var phone: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val userRole: UserRole = UserRole.ROLE_USER,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val userType: UserType = UserType.NORMAL,

    @Column
    @Enumerated(EnumType.STRING)
    val oAuth2Provider: OAuth2Provider? = null
) : BaseEntity() {

    companion object {
        fun createNormalUser(
            email: String,
            password: String,
            name: String,
            nickname: String,
            phone: String
        ): User {
            return User(
                email = email,
                password = password,
                name = name,
                nickname = nickname,
                phone = phone
            )
        }
    }
}

enum class UserRole() {
    ROLE_ADMIN, ROLE_USER, ROLE_PARTNER
}

enum class UserType() {
    NORMAL, SOCIAL
}

enum class OAuth2Provider() {
    NAVER, KAKAO, GOOGLE
}

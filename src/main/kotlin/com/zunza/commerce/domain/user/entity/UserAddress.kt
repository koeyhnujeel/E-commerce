package com.zunza.commerce.domain.user.entity

import com.zunza.commerce.support.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "user_addresses")
class UserAddress(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    val user: User,

    @Column(nullable = false)
    val alias: String = "",

    @Column(nullable = false)
    var address: String = "",

    @Column(nullable = false)
    var addressDetail: String = "",

    @Column(nullable = false)
    var postalCode: Int = 0,

    @Column(nullable = false)
    var isDefault: Boolean = true
) : BaseEntity() {
}

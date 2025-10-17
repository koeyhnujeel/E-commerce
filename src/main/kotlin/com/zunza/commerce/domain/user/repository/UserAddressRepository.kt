package com.zunza.commerce.domain.user.repository

import com.zunza.commerce.domain.user.entity.UserAddress
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAddressRepository : JpaRepository<UserAddress, Long> {
}

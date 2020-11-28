package com.finance.control.repository

import com.finance.control.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun findByEmail(email: String?): User?
    fun findByEmailAndPassword(email: String?, password: String?): User?

}

package com.finance.control.repository

import com.finance.control.model.Authenticate
import com.finance.control.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface AuthenticateRepository : JpaRepository<Authenticate, Long> {

    fun findByEmail( email: String ): Authenticate?
    fun deleteByCodeAndEmail( code: String, email: String )
    @Transactional
    @Modifying
    @Query( "DELETE FROM Authenticate a where a.email = ?1")
    fun deleteByEmail( email: String )
}
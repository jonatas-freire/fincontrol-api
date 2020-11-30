package com.finance.control.repository

import com.finance.control.model.TransactionHistory
import com.finance.control.model.TransactionType
import com.finance.control.model.User
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
interface TransactionHistoryRepository : CrudRepository<TransactionHistory, Long> {


    fun findByIdAndUser(id: Long, user: User): TransactionHistory?
    fun findByUser(user: User):  List<TransactionHistory>?
    fun findByUserAndType( user: User, type: TransactionType ):  List<TransactionHistory>?
    @Transactional
    @Modifying
    @Query("DELETE FROM TransactionHistory t where t.id = ?1 and t.user.email = ?2 ")
    fun deleteByIdAndEmail(id: Long, email: String)
}
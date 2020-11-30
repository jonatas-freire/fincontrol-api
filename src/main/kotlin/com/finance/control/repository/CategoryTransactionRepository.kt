package com.finance.control.repository

import com.finance.control.model.CategoryTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*
import javax.transaction.Transactional

@Repository
interface CategoryTransactionRepository : JpaRepository<CategoryTransaction, Long> {

    fun findById(id: Int): CategoryTransaction?
}
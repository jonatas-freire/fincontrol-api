package com.finance.control.service

import com.finance.control.model.*
import com.finance.control.repository.CategoryTransactionRepository
import com.finance.control.repository.TransactionHistoryRepository
import com.finance.control.validation.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class TransactionService {

    @Autowired
    private lateinit var transactionRepository: TransactionHistoryRepository

    @Autowired
    private lateinit var userService: UserService

    @Autowired
    private lateinit var categoryTransactionRepository: CategoryTransactionRepository


    fun listAll(): Validation<List<TransactionHistory>?, TransactionListStatus> {
        return try {
            val user = userService.getCurrentUser()
                    ?: return Validation(TransactionListStatus.ERROR)

            val transactions = transactionRepository.findByUser(user)
                    ?: return Validation(TransactionListStatus.NOT_FOUND)

            Validation(TransactionListStatus.FOUND, transactions )
        } catch (e: Exception) {
            Validation( TransactionListStatus.ERROR )
        }
    }

    fun listByType(type: TransactionType): Validation<List<TransactionHistory>?, TransactionListStatus> {
        return try {
            val user = userService.getCurrentUser()
                    ?: return Validation(TransactionListStatus.ERROR)

            val transactions = transactionRepository.findByUserAndType(user, type)
                    ?: return Validation(TransactionListStatus.NOT_FOUND)

            Validation(TransactionListStatus.FOUND, transactions )
        } catch (e: Exception) {
            Validation( TransactionListStatus.ERROR )
        }
    }

    fun create(transaction: Transaction): Validation<TransactionHistory?, TransactionCreateStatus> {
        return try {
            val user = userService.getCurrentUser()
                    ?: return Validation(TransactionCreateStatus.ERROR)

            val category = categoryTransactionRepository.findById(transaction.category)
                    ?: return Validation(TransactionCreateStatus.CATEGORY_NOT_FOUND)


             val normalizedTransaction = TransactionHistory(
                     amount = transaction.amount,
                     name = transaction.name,
                     description = transaction.description,
                     date = transaction.date,
                     user = user,
                     category = category,
                     type = transaction.type
             )
             val savedTransaction = transactionRepository.save(normalizedTransaction)
             userService.updateBalance(user, savedTransaction)

              Validation( TransactionCreateStatus.TRANSACTION_CREATED, savedTransaction)

        } catch (e: Exception) {
            Validation( TransactionCreateStatus.ERROR )
        }
    }

    fun edit(id: Long, transactionUpdate: Transaction ): Validation<Boolean, TransactionEditStatus> {
        return try {
            val user = userService.getCurrentUser()
                    ?: return Validation(TransactionEditStatus.ERROR)

            val transaction = transactionRepository.findByIdAndUser(id, user)
                    ?: return Validation( TransactionEditStatus.TRANSACTION_NOT_FOUND )

            val normalizedTransaction = transaction.copy(
                    type = when( transaction.type ) {
                        TransactionType.SPENT -> TransactionType.RECEIPT
                        TransactionType.RECEIPT -> TransactionType.SPENT
                    }
            )

            userService.updateBalance(user, normalizedTransaction)

            val category = categoryTransactionRepository.findById(transactionUpdate.category)
                    ?: return Validation(TransactionEditStatus.CATEGORY_NOT_FOUND)

            val updatedTransaction = transaction.copy(
                    amount = transactionUpdate.amount,
                    name = transactionUpdate.name,
                    description = transactionUpdate.description,
                    date = transactionUpdate.date,
                    user = user,
                    category = category,
                    type = transactionUpdate.type
            )

            transactionRepository.save(updatedTransaction)
            userService.updateBalance(user, updatedTransaction)

            Validation( TransactionEditStatus.TRANSACTION_EDITED, true )
        } catch (e: Exception) {
            Validation(TransactionEditStatus.ERROR)
        }
    }

    fun delete(id: Long): Validation<Boolean, TransactionDeleteStatus> {
        return try {
            val user = userService.getCurrentUser()
                    ?: return Validation(TransactionDeleteStatus.ERROR)

            val transaction = transactionRepository.findByIdAndUser(id, user)
                    ?: return Validation( TransactionDeleteStatus.TRANSACTION_NOT_FOUND )

            val normalizedTransaction = transaction.copy(
                    type = when( transaction.type ) {
                        TransactionType.SPENT -> TransactionType.RECEIPT
                        TransactionType.RECEIPT -> TransactionType.SPENT
                    }
            )
            userService.updateBalance(user, normalizedTransaction)
            transactionRepository.deleteByIdAndEmail(id, user.email)

            Validation( TransactionDeleteStatus.TRANSACTION_DELETED, true )
        } catch (e: Exception) {
            Validation(TransactionDeleteStatus.ERROR)
        }
    }

    fun findOne(id: Long): TransactionHistory? {
        val user =  userService.getCurrentUser()
                ?: return null
        return transactionRepository.findByIdAndUser(id,user);
    }
}
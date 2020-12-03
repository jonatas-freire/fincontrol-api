package com.finance.control.controller

import com.finance.control.dto.DTO
import com.finance.control.dto.DTOTransactionCreate
import com.finance.control.model.Transaction
import com.finance.control.model.TransactionHistory
import com.finance.control.model.TransactionType
import com.finance.control.service.TransactionService
import com.finance.control.validation.TransactionCreateStatus
import com.finance.control.validation.TransactionDeleteStatus
import com.finance.control.validation.TransactionEditStatus
import com.finance.control.validation.TransactionListStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/transaction")
class TransactionController {


    @Autowired
    lateinit var transactionService: TransactionService

    @GetMapping("/all")
    fun list(): ResponseEntity<DTO<List<TransactionHistory>?>> {
        val transactions = transactionService.listAll()
        val dto: DTO<List<TransactionHistory>?> = when (transactions.status) {
            TransactionListStatus.FOUND ->
                DTO(
                        status = 200,
                        content = transactions.result,
                        message = "Aqui estão todas as transações encontradas!"
                )
            TransactionListStatus.NOT_FOUND ->
                DTO(
                        status = 404, message = "Não foram encontrada transações para esse usuário!"
                )
            TransactionListStatus.ERROR -> DTO( status =  500, message = "Houve um erro no servidor!")
        }
        return ResponseEntity.status(dto.status).body(dto)
    }

    @GetMapping("/all/{type}")
    fun listByType(@PathVariable( name = "type" ) type: TransactionType): ResponseEntity<DTO<List<TransactionHistory>?>> {
        val transactions = transactionService.listByType(type)
        val dto: DTO<List<TransactionHistory>?> = when (transactions.status) {
            TransactionListStatus.FOUND ->
                DTO(
                        status = 200,
                        content = transactions.result,
                        message = "Aqui estão todas as transações encontradas do tipo $type!"
                )
            TransactionListStatus.NOT_FOUND ->
                DTO(
                        status = 404, message = "Não foram encontrada transações do tipo $type para esse usuário!"
                )
            TransactionListStatus.ERROR -> DTO( status =  500, message = "Houve um erro no servidor!")
        }
        return ResponseEntity.status(dto.status).body(dto)

    }

    @PostMapping("/create")
    fun create(@RequestBody body: Transaction): ResponseEntity<DTO<TransactionHistory?>> {
        val createTransaction = transactionService.create(body)
        val dto: DTO<TransactionHistory?> = when (createTransaction.status) {
            TransactionCreateStatus.TRANSACTION_CREATED ->
                DTO( status = 200, content = createTransaction.result, message = "Transação criada!")
            TransactionCreateStatus.CATEGORY_NOT_FOUND ->
                DTO( status = 404, message = "Categoria não encontrada!")
            TransactionCreateStatus.ERROR ->
                DTO( status = 500, message = "Houve um erro no servidor!")
        }

        return ResponseEntity.status(dto.status).body(dto)
    }

    @PutMapping("/edit/{id}")
    fun edit(@PathVariable( name = "id") id: Long, @RequestBody body: Transaction ): ResponseEntity<DTO<Boolean?>> {
        val deleteTransaction = transactionService.edit(id, body)
        val dto: DTO<Boolean?> = when (deleteTransaction.status) {
            TransactionEditStatus.TRANSACTION_EDITED ->
                DTO( status = 200, content = true, message = "Transação editada!")
            TransactionEditStatus.TRANSACTION_NOT_FOUND ->
                DTO( status = 404, message = "Transação não encontrada!")
            TransactionEditStatus.CATEGORY_NOT_FOUND ->
                DTO( status = 404, message = "Categoria não encontrada!")
            TransactionEditStatus.ERROR -> DTO( status = 500, message = "Houve um erro no servidor!")

        }
        return ResponseEntity.status(dto.status).body(dto)
    }

    @DeleteMapping("/delete/{id}")
    fun delete(@PathVariable( name = "id") id: Long): ResponseEntity<DTO<Boolean?>> {
        val deleteTransaction = transactionService.delete(id)
        val dto: DTO<Boolean?> = when (deleteTransaction.status) {
            TransactionDeleteStatus.TRANSACTION_DELETED ->
                DTO( status = 200, content = true, message = "Transação deletada!")
            TransactionDeleteStatus.TRANSACTION_NOT_FOUND ->
                DTO( status = 404, message = "Transação não encontrada!")
            TransactionDeleteStatus.ERROR -> DTO( status = 500, message = "Houve um erro no servidor!")
        }
        return ResponseEntity.status(dto.status).body(dto)
    }
}

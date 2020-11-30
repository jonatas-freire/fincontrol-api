package com.finance.control.dto

import com.finance.control.model.CategoryTransaction
import com.finance.control.model.TransactionType
import java.util.*

class DTOTransactionCreate (
    val name: String,
    val date: Calendar,
    val monthly: Boolean = false,
    val amount: Double,
    val category: CategoryTransaction,
    val type: TransactionType
)
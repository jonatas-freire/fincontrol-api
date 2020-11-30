package com.finance.control.model

import com.fasterxml.jackson.annotation.JsonFormat
import org.springframework.format.annotation.DateTimeFormat
import java.util.*

class Transaction (
         val name: String,
         val description: String,
         @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss")
         val date: Calendar,
         val monthly: Boolean,
         val amount: Double,
         val category: Int,
         val type: TransactionType
)
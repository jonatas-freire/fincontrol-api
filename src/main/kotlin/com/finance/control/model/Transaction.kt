package com.finance.control.model

import java.util.*

interface Transaction {
    val id: Long
    val name: String
    val date: Calendar
    val monthly: Boolean
    val category: Category

    val user: User
}
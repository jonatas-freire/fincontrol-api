package com.finance.control.dto

class DTO<T> (
    val status: Int,
    val content: T? = null,
    val message: String,
)
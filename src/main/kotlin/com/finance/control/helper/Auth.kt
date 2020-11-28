package com.finance.control.helper

import kotlin.streams.asSequence

class Auth {
    private val source = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    fun generateCode(outputStrLength: Long) =
            java.util.Random()
                .ints(outputStrLength, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")

}
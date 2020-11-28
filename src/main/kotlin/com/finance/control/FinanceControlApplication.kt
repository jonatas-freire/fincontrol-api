package com.finance.control

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
class FinanceControlApplication

fun main(args: Array<String>) {
	runApplication<FinanceControlApplication>(*args)
}

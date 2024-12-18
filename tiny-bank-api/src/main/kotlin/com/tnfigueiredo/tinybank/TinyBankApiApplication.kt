package com.tnfigueiredo.tinybank

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TinyBankApiApplication

fun main(args: Array<String>) {
	runApplication<TinyBankApiApplication>(*args)
}

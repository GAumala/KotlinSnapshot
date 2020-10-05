package com.example.springconsumer

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringConsumerApplication

fun main(args: Array<String>) {
    runApplication<SpringConsumerApplication>(*args)
}

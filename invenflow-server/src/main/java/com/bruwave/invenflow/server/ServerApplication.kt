package com.bruwave.invenflow.server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class ServerApplication
fun main(args: Array<String>) {
    SpringApplication.run(ServerApplication::class.java, *args)
}
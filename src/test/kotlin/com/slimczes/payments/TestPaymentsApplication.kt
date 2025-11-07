package com.slimczes.payments

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
    fromApplication<PaymentsApplication>().with(TestcontainersConfiguration::class).run(*args)
}

package com.slimczes.payments.api

import com.slimczes.payments.domain.Money
import com.slimczes.payments.service.client.ClientService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController("/payments")
class PaymentController (
    private val clientService: ClientService
) {

    @PostMapping
    fun create(@RequestBody money: Money): ResponseEntity<CreateClientResponse> {
        val createClientResponse = clientService.createClient(money)
        return ResponseEntity.ok(createClientResponse)
    }

}
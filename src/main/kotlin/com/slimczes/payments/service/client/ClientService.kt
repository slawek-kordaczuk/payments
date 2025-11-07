package com.slimczes.payments.service.client

import com.slimczes.payments.api.CreateClientResponse
import com.slimczes.payments.dao.ClientRepository
import com.slimczes.payments.domain.Money
import com.slimczes.payments.domain.client.Client
import com.slimczes.payments.domain.client.Wallet
import org.springframework.stereotype.Service

@Service
class ClientService(
    private val clientRepository: ClientRepository
) {
    fun createClient(amount: Money): CreateClientResponse {
        val wallet = Wallet.create(amount)
        val client = Client.create(wallet)
        clientRepository.save(client)
        return CreateClientResponse(client.id, wallet)
    }
}
package com.slimczes.payments.dao

import com.slimczes.payments.domain.client.Client
import java.util.UUID

interface ClientRepository {
    fun findById(id: UUID): Client?
    fun save(client: Client): Client
    fun update(client: Client): Client
    fun delete(client: Client)
}
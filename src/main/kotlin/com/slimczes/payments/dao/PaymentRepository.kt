package com.slimczes.payments.dao

import com.slimczes.payments.domain.Payment
import java.util.UUID

interface PaymentRepository {

    fun save(payment: Payment): Payment
    fun findByOrderId(orderId: UUID): Payment?
    fun update(payment: Payment): Payment
    fun deleteById(id: UUID): Boolean

}
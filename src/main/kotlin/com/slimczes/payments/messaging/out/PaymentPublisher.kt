package com.slimczes.payments.messaging.out

import com.slimczes.payments.messaging.out.event.PaidEvent
import com.slimczes.payments.messaging.out.event.PaidFailedEvent

interface PaymentPublisher {

    fun publishPaidEvent(paidEvent: PaidEvent)
    fun publishPaidFailedEvent(paidFailedEvent: PaidFailedEvent)
}
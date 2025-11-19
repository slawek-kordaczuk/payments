package com.slimczes.payments.messaging.out

import com.slimczes.payments.messaging.out.event.PaidEvent
import com.slimczes.payments.messaging.out.event.PaidFailedEvent
import com.slimczes.payments.messaging.out.event.PublishEvent
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.Objects

@Component
class PaymentPublisher(
    private val kafkaTemplate: KafkaTemplate<String, PublishEvent>,
    @param:Value("\${kafka.topics.paid}")
    private val paidTopic: String,
    @param:Value("\${kafka.topics.paid-failed}")
    private val paidFailedTopic: String
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun publishPaidEvent(paidEvent: PaidEvent) {
        log.info("Publishing PaidEvent to topic $paidTopic: $paidEvent")
        kafkaTemplate.send(paidTopic, paidEvent).whenComplete { _, ex ->
            if (Objects.nonNull(ex)) {
                log.error("Failed to publish PaidEvent to topic $paidTopic: $paidEvent", ex)
            } else {
                log.info("Successfully published PaidEvent to topic $paidTopic: $paidEvent")
            }
        }
    }

    fun publishPaidFailedEvent(paidFailedEvent: PaidFailedEvent) {
        log.info("Publishing PaidFailedEvent to topic $paidFailedEvent")
        kafkaTemplate.send(paidFailedTopic, paidFailedEvent).whenComplete { _, ex ->
            if (Objects.nonNull(ex)) {
                log.error("Failed to publish PaidFailedEvent to topic $paidFailedTopic: $paidFailedEvent", ex)
            } else {
                log.info("Successfully published PaidFailedEvent to topic $paidFailedTopic: $paidFailedEvent")
            }
        }
    }


}

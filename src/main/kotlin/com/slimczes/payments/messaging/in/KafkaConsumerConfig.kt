package com.slimczes.payments.messaging.`in`

import com.slimczes.payments.messaging.`in`.event.PaymentCancelEvent
import com.slimczes.payments.messaging.`in`.event.PaymentEvent
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer

@Configuration
internal class KafkaConsumerConfig(
    @param:Value("\${spring.kafka.bootstrap-servers}")
    private val bootstrapServers: String,
    @param:Value("\${spring.kafka.consumer.group-id}")
    private val groupId: String
) {

    private fun baseConsumerConfig(): MutableMap<String, Any> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.GROUP_ID_CONFIG] = groupId
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        props[JsonDeserializer.TRUSTED_PACKAGES] = "*"
        props[JsonDeserializer.USE_TYPE_INFO_HEADERS] = false
        return props
    }

    @Bean
    fun createPaymentConsumerFactory(): ConsumerFactory<String, PaymentEvent> {
        val props = baseConsumerConfig()
        return DefaultKafkaConsumerFactory(
            props,
            StringDeserializer(),
            JsonDeserializer(PaymentEvent::class.java, false)
        )
    }

    @Bean
    fun createPaymentKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, PaymentEvent> =
            ConcurrentKafkaListenerContainerFactory<String, PaymentEvent>()
        factory.consumerFactory = createPaymentConsumerFactory()
        return factory
    }

    @Bean
    fun cancelPaymentConsumerFactory(): ConsumerFactory<String, PaymentCancelEvent> {
        val props = baseConsumerConfig()
        return DefaultKafkaConsumerFactory(
            props,
            StringDeserializer(),
            JsonDeserializer(PaymentCancelEvent::class.java, false)
        )
    }

    @Bean
    fun cancelPaymentKafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, PaymentCancelEvent> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, PaymentCancelEvent> =
            ConcurrentKafkaListenerContainerFactory<String, PaymentCancelEvent>()
        factory.consumerFactory = cancelPaymentConsumerFactory()
        return factory
    }

}
package com.slimczes.payments.service

import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import java.time.Duration
import java.util.HashMap
import java.util.UUID

internal fun <T> createConsumer(targetType: Class<T>, topic: String, bootstrapServers: String): Consumer<String, T> {
    val consumerProps = createConsumerProps(bootstrapServers, targetType)
    val factory: DefaultKafkaConsumerFactory<String, T> = DefaultKafkaConsumerFactory(consumerProps)
    val consumer: Consumer<String, T> = factory.createConsumer()
    consumer.subscribe(listOf(topic))
    consumer.poll(Duration.ofMillis(1000))
    return consumer
}

private fun createConsumerProps(bootstrapServers: String?, targetType: Class<*>): MutableMap<String?, Any?> {
    val props: MutableMap<String?, Any?> = HashMap<String?, Any?>()
    props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
    props[ConsumerConfig.GROUP_ID_CONFIG] = "test-group-" + UUID.randomUUID()
    props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
    props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
    props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
    props[JsonDeserializer.TRUSTED_PACKAGES] = "*"
    props[JsonDeserializer.USE_TYPE_INFO_HEADERS] = false
    props[JsonDeserializer.VALUE_DEFAULT_TYPE] = targetType.getName()
    return props
}
package com.slimczes.payments.service

import com.slimczes.payments.TestcontainersConfiguration
import com.slimczes.payments.dao.ClientRepository
import com.slimczes.payments.dao.PaymentRepository
import com.slimczes.payments.domain.Money
import com.slimczes.payments.messaging.out.event.PaidEvent
import com.slimczes.payments.service.dto.CreatePaymentDto
import com.slimczes.payments.service.payment.PaymentService
import org.apache.kafka.clients.consumer.Consumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.apache.kafka.common.serialization.StringDeserializer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.test.context.TestPropertySource
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.kafka.KafkaContainer
import java.math.BigDecimal
import java.time.Duration
import java.util.*

@SpringBootTest
@Import(TestcontainersConfiguration::class)
@TestPropertySource(
    properties = ["logging.level.org.apache.kafka=WARN"
    ]
)
@Transactional
class PaymentServiceIT(
    @Autowired private val paymentService: PaymentService,
    @Autowired private val clientRepository: ClientRepository,
    @Autowired private val paymentRepository: PaymentRepository,
    @Autowired private val kafkaContainer: KafkaContainer,
    @param:Value("\${kafka.topics.paid}")
    private val paidTopic: String,
    @param:Value("\${kafka.topics.paid-failed}")
    private val paidFailedTopic: String
) {

    @Test
    fun createPayment() {

        val orderId = UUID.randomUUID()
        val clientId = UUID.fromString("22222222-2222-2222-2222-222222222222")
        val money = Money(amount = BigDecimal("100.00"), currency = "PLN")
        val createPaymentDto = CreatePaymentDto(
            clientId = clientId,
            orderId = orderId,
            amount = money
        )

        val paidConsumer = createConsumer(PaidEvent::class.java, paidTopic)
        paymentService.createPayment(createPaymentDto)
        val records: ConsumerRecords<String?, PaidEvent?> = paidConsumer.poll(Duration.ofSeconds(5))
        assertThat(records).hasSize(1)

        val record: ConsumerRecord<String?, PaidEvent?> = records.iterator().next()
        assertThat(record.topic()).isEqualTo(paidTopic)

        val event: PaidEvent? = record.value()
        assertThat(event?.orderId).isEqualTo(orderId)
        paidConsumer.close()

        val payment = paymentRepository.findByOrderId(orderId)
        assertThat (payment?.orderId).isEqualTo(event?.orderId)

    }

    private fun <T> createConsumer(targetType: Class<T>, topic: String): Consumer<String?, T?> {
        val bootstrapServers = kafkaContainer.bootstrapServers
        val consumerProps = createConsumerProps(bootstrapServers, targetType)
        val factory: DefaultKafkaConsumerFactory<String?, T?> = DefaultKafkaConsumerFactory(consumerProps)
        val consumer: Consumer<String?, T?> = factory.createConsumer()
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


}
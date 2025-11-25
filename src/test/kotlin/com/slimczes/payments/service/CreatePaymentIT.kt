package com.slimczes.payments.service

import com.slimczes.payments.TestcontainersConfiguration
import com.slimczes.payments.dao.PaymentRepository
import com.slimczes.payments.domain.Money
import com.slimczes.payments.messaging.out.event.PaidEvent
import com.slimczes.payments.service.dto.CreatePaymentDto
import com.slimczes.payments.service.payment.CreatePayment
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.ConsumerRecords
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
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
class CreatePaymentIT(
    @Autowired private val createPayment: CreatePayment,
    @Autowired private val paymentRepository: PaymentRepository,
    @Autowired private val kafkaContainer: KafkaContainer,
    @param:Value("\${kafka.topics.paid}")
    private val paidTopic: String
) {

    @Test
    fun createPayment(): Unit = runBlocking {
        // Given
        val orderId = UUID.randomUUID()
        val clientId = UUID.fromString("44444444-4444-4444-4444-444444444444")
        val money = Money(amount = BigDecimal("100.00"), currency = "PLN")
        val createPaymentDto = CreatePaymentDto(
            clientId = clientId,
            orderId = orderId,
            amount = money
        )

        val paidConsumer = createConsumer(PaidEvent::class.java, paidTopic, kafkaContainer.bootstrapServers)

        // When
        createPayment.createPayment(createPaymentDto)

        // Then
        val records: ConsumerRecords<String?, PaidEvent?> = paidConsumer.poll(Duration.ofSeconds(5))
        assertThat(records).hasSize(1)

        val record: ConsumerRecord<String?, PaidEvent?> = records.iterator().next()
        assertThat(record.topic()).isEqualTo(paidTopic)

        val event: PaidEvent? = record.value()
        assertThat(event?.orderId).isEqualTo(orderId)
        paidConsumer.close()

        val payment = paymentRepository.findByOrderId(orderId)
        assertThat(payment?.orderId).isEqualTo(event?.orderId)
        val paymentWallet = payment?.getClient()?.getWallet()
        assertThat(paymentWallet?.getBalance()).isEqualTo(Money(amount = BigDecimal("400.0000"), currency = "PLN"))
    }


}
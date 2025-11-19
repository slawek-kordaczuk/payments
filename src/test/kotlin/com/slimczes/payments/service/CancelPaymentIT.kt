package com.slimczes.payments.service

import com.slimczes.payments.TestcontainersConfiguration
import com.slimczes.payments.dao.PaymentRepository
import com.slimczes.payments.domain.Money
import com.slimczes.payments.messaging.out.event.PaidEvent
import com.slimczes.payments.service.dto.CancelPaymentDto
import com.slimczes.payments.service.dto.CreatePaymentDto
import com.slimczes.payments.service.payment.CancelPayment
import com.slimczes.payments.service.payment.CreatePayment
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
import java.util.UUID

@SpringBootTest
@Import(TestcontainersConfiguration::class)
@TestPropertySource(
    properties = ["logging.level.org.apache.kafka=WARN"
    ]
)
@Transactional
class CancelPaymentIT(
    @Autowired private val createPayment: CreatePayment,
    @Autowired private val cancelPayment: CancelPayment,
    @Autowired private val paymentRepository: PaymentRepository,
    @Autowired private val kafkaContainer: KafkaContainer,
    @param:Value("\${kafka.topics.paid}")
    private val paidTopic: String,
) {

    @Test
    fun cancelPayment() {
        // Given
        val orderId = UUID.randomUUID()
        val clientId = UUID.fromString("55555555-5555-5555-5555-555555555555")
        val money = Money(amount = BigDecimal("1000.00"), currency = "EUR")
        val createPaymentDto = CreatePaymentDto(
            clientId = clientId,
            orderId = orderId,
            amount = money
        )

        val paidConsumer = createConsumer(PaidEvent::class.java, paidTopic, kafkaContainer.bootstrapServers)
        createPayment.createPayment(createPaymentDto)
        val records: ConsumerRecords<String?, PaidEvent?> = paidConsumer.poll(Duration.ofSeconds(5))
        assertThat(records).hasSize(1)

        val record: ConsumerRecord<String?, PaidEvent?> = records.iterator().next()
        assertThat(record.topic()).isEqualTo(paidTopic)

        val event: PaidEvent? = record.value()
        assertThat(event?.orderId).isEqualTo(orderId)
        paidConsumer.close()


        val cancelPaymentDto = CancelPaymentDto(
            clientId = clientId,
            orderId = orderId,
        )

        // When
        cancelPayment.cancelPayment(cancelPaymentDto)

        // Then
        val canceledPayment = paymentRepository.findByOrderId(orderId)
        assertThat (canceledPayment?.orderId).isEqualTo(event?.orderId)
        val paymentWallet = canceledPayment?.getClient()?.getWallet()
        assertThat(paymentWallet?.getBalance()).isEqualTo(Money(amount = BigDecimal("2500.0000"), currency = "EUR"))
    }
}
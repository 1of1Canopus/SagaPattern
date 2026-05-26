package io.housedevinci.sagapattern.choreography;

import io.housedevinci.sagapattern.choreography.config.KafkaTopics;
import io.housedevinci.sagapattern.choreography.event.*;
import io.housedevinci.sagapattern.choreography.producer.OrderEventProducer;
import io.housedevinci.sagapattern.config.AxonTestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(
    partitions = 1,
    topics = {
        KafkaTopics.ORDERS_PLACED, KafkaTopics.PAYMENT_PROCESSED, KafkaTopics.PAYMENT_FAILED,
        KafkaTopics.PAYMENT_CANCELLED, KafkaTopics.INVENTORY_RESERVED, KafkaTopics.INVENTORY_FAILED,
        KafkaTopics.ORDER_SHIPPED, KafkaTopics.ORDER_CANCELLED
    }
)
@TestPropertySource(properties = {
    "axon.axonserver.enabled=false",
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@Import(AxonTestConfig.class)
class ChoreographyIntegrationTest {

    @TestConfiguration
    static class TestConsumers {
        final BlockingQueue<OrderShippedEvent> shipped = new LinkedBlockingQueue<>();
        final BlockingQueue<OrderCancelledEvent> cancelled = new LinkedBlockingQueue<>();

        @KafkaListener(topics = KafkaTopics.ORDER_SHIPPED, groupId = "it-shipped")
        public void onShipped(OrderShippedEvent event) { shipped.add(event); }

        @KafkaListener(topics = KafkaTopics.ORDER_CANCELLED, groupId = "it-cancelled")
        public void onCancelled(OrderCancelledEvent event) { cancelled.add(event); }
    }

    @Autowired private OrderEventProducer producer;
    @Autowired private TestConsumers consumers;

    @BeforeEach
    void clearQueues() {
        consumers.shipped.clear();
        consumers.cancelled.clear();
    }

    @Test
    void happyPath_orderEventuallyShipped() throws InterruptedException {
        var orderId = UUID.randomUUID().toString();
        producer.publishOrderPlaced(new OrderPlacedEvent(orderId, "c1", "p1", 1, 50.0, null));

        var event = consumers.shipped.poll(15, TimeUnit.SECONDS);
        assertThat(event).isNotNull();
        assertThat(event.orderId()).isEqualTo(orderId);
    }

    @Test
    void paymentFailure_orderCancelled() throws InterruptedException {
        var orderId = UUID.randomUUID().toString();
        producer.publishOrderPlaced(new OrderPlacedEvent(orderId, "c1", "p1", 1, 50.0, "PAYMENT"));

        var event = consumers.cancelled.poll(15, TimeUnit.SECONDS);
        assertThat(event).isNotNull();
        assertThat(event.orderId()).isEqualTo(orderId);
    }

    @Test
    void inventoryFailure_orderCancelledAfterPaymentCompensation() throws InterruptedException {
        var orderId = UUID.randomUUID().toString();
        producer.publishOrderPlaced(new OrderPlacedEvent(orderId, "c1", "p1", 1, 50.0, "INVENTORY"));

        var event = consumers.cancelled.poll(15, TimeUnit.SECONDS);
        assertThat(event).isNotNull();
        assertThat(event.orderId()).isEqualTo(orderId);
    }
}

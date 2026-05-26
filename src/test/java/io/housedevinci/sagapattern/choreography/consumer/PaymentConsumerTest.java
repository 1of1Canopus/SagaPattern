package io.housedevinci.sagapattern.choreography.consumer;

import io.housedevinci.sagapattern.choreography.config.KafkaTopics;
import io.housedevinci.sagapattern.choreography.event.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PaymentConsumerTest {

    @Mock KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks PaymentConsumer paymentConsumer;

    @Test
    void handleOrderPlaced_happyPath_shouldPublishPaymentProcessed() {
        var event = new OrderPlacedEvent("order-1", "c1", "p1", 1, 50.0, null);
        paymentConsumer.handleOrderPlaced(event);
        verify(kafkaTemplate).send(eq(KafkaTopics.PAYMENT_PROCESSED), eq("order-1"), any(PaymentProcessedEvent.class));
    }

    @Test
    void handleOrderPlaced_simulatePaymentFailure_shouldPublishPaymentFailed() {
        var event = new OrderPlacedEvent("order-1", "c1", "p1", 1, 50.0, "PAYMENT");
        paymentConsumer.handleOrderPlaced(event);
        verify(kafkaTemplate).send(eq(KafkaTopics.PAYMENT_FAILED), eq("order-1"), any(PaymentFailedEvent.class));
    }

    @Test
    void handleInventoryFailed_shouldPublishPaymentCancelled() {
        var event = new InventoryFailedEvent("inv-1", "payment-1", "order-1", "Simulated");
        paymentConsumer.handleInventoryFailed(event);
        verify(kafkaTemplate).send(eq(KafkaTopics.PAYMENT_CANCELLED), eq("order-1"), any(PaymentCancelledEvent.class));
    }
}

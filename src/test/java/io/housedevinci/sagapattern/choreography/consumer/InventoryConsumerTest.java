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
class InventoryConsumerTest {

    @Mock KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks InventoryConsumer inventoryConsumer;

    @Test
    void handlePaymentProcessed_happyPath_shouldPublishInventoryReserved() {
        var event = new PaymentProcessedEvent("pay-1", "order-1", 50.0, null);
        inventoryConsumer.handlePaymentProcessed(event);
        verify(kafkaTemplate).send(eq(KafkaTopics.INVENTORY_RESERVED), eq("order-1"), any(InventoryReservedEvent.class));
    }

    @Test
    void handlePaymentProcessed_simulateInventoryFailure_shouldPublishInventoryFailed() {
        var event = new PaymentProcessedEvent("pay-1", "order-1", 50.0, "INVENTORY");
        inventoryConsumer.handlePaymentProcessed(event);
        verify(kafkaTemplate).send(eq(KafkaTopics.INVENTORY_FAILED), eq("order-1"), any(InventoryFailedEvent.class));
    }
}

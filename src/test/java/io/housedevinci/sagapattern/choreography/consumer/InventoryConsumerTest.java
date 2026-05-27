package io.housedevinci.sagapattern.choreography.consumer;

import io.housedevinci.sagapattern.choreography.config.KafkaTopics;
import io.housedevinci.sagapattern.choreography.event.*;
import io.housedevinci.sagapattern.choreography.inventory.ProductStock;
import io.housedevinci.sagapattern.choreography.inventory.ProductStockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryConsumerTest {

    @Mock KafkaTemplate<String, Object> kafkaTemplate;
    @Mock ProductStockRepository stockRepository;
    @InjectMocks InventoryConsumer inventoryConsumer;

    @Test
    void handlePaymentProcessed_happyPath_shouldPublishInventoryReserved() {
        when(stockRepository.findById("p1")).thenReturn(Optional.of(new ProductStock("p1", 100)));
        var event = new PaymentProcessedEvent("pay-1", "order-1", 50.0, null, "p1", 1);
        inventoryConsumer.handlePaymentProcessed(event);
        verify(kafkaTemplate).send(eq(KafkaTopics.INVENTORY_RESERVED), eq("order-1"), any(InventoryReservedEvent.class));
    }

    @Test
    void handlePaymentProcessed_simulateInventoryFailure_shouldPublishInventoryFailed() {
        var event = new PaymentProcessedEvent("pay-1", "order-1", 50.0, "INVENTORY", "p1", 1);
        inventoryConsumer.handlePaymentProcessed(event);
        verify(kafkaTemplate).send(eq(KafkaTopics.INVENTORY_FAILED), eq("order-1"), any(InventoryFailedEvent.class));
    }
}

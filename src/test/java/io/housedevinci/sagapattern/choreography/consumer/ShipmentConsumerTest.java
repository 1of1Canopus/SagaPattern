package io.housedevinci.sagapattern.choreography.consumer;

import io.housedevinci.sagapattern.choreography.config.KafkaTopics;
import io.housedevinci.sagapattern.choreography.event.InventoryReservedEvent;
import io.housedevinci.sagapattern.choreography.event.OrderShippedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ShipmentConsumerTest {

    @Mock KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks ShipmentConsumer shipmentConsumer;

    @Test
    void handleInventoryReserved_shouldPublishOrderShipped() {
        var event = new InventoryReservedEvent("inv-1", "order-1", "p1", 1);
        shipmentConsumer.handleInventoryReserved(event);
        verify(kafkaTemplate).send(eq(KafkaTopics.ORDER_SHIPPED), eq("order-1"), any(OrderShippedEvent.class));
    }
}

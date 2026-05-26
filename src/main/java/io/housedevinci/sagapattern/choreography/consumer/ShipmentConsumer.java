package io.housedevinci.sagapattern.choreography.consumer;

import io.housedevinci.sagapattern.choreography.config.KafkaTopics;
import io.housedevinci.sagapattern.choreography.event.InventoryReservedEvent;
import io.housedevinci.sagapattern.choreography.event.OrderShippedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ShipmentConsumer {

    private static final Logger log = LoggerFactory.getLogger(ShipmentConsumer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ShipmentConsumer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopics.INVENTORY_RESERVED, groupId = "shipment-service")
    public void handleInventoryReserved(InventoryReservedEvent event) {
        String shipmentId = UUID.randomUUID().toString();
        log.info("[CHOREOGRAPHY] ShipmentConsumer: shipping order {} via {}", event.orderId(), shipmentId);
        kafkaTemplate.send(KafkaTopics.ORDER_SHIPPED, event.orderId(),
            new OrderShippedEvent(shipmentId, event.orderId()));
        log.info("[CHOREOGRAPHY] Order {} SHIPPED — SAGA COMPLETE", event.orderId());
    }
}

package io.housedevinci.sagapattern.choreography.consumer;

import io.housedevinci.sagapattern.choreography.config.KafkaTopics;
import io.housedevinci.sagapattern.choreography.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class InventoryConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryConsumer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public InventoryConsumer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_PROCESSED, groupId = "inventory-service")
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("[CHOREOGRAPHY] InventoryConsumer: reserving inventory for order {}", event.orderId());

        if ("INVENTORY".equals(event.simulateFailure())) {
            log.info("[CHOREOGRAPHY] InventoryConsumer: simulating failure for order {}", event.orderId());
            // paymentId is forwarded so PaymentConsumer can cancel the right payment
            kafkaTemplate.send(KafkaTopics.INVENTORY_FAILED, event.orderId(),
                new InventoryFailedEvent(UUID.randomUUID().toString(), event.paymentId(), event.orderId(), "Simulated inventory failure"));
        } else {
            String inventoryId = UUID.randomUUID().toString();
            log.info("[CHOREOGRAPHY] InventoryConsumer: inventory {} reserved for order {}", inventoryId, event.orderId());
            kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVED, event.orderId(),
                new InventoryReservedEvent(inventoryId, event.orderId(), "product", event.amount() > 0 ? 1 : 0));
        }
    }
}

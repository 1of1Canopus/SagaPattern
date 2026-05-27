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
public class PaymentConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentConsumer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopics.ORDERS_PLACED, groupId = "payment-service")
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("[CHOREOGRAPHY] PaymentConsumer: processing payment for order {}", event.orderId());

        if ("PAYMENT".equals(event.simulateFailure())) {
            log.info("[CHOREOGRAPHY] PaymentConsumer: simulating failure for order {}", event.orderId());
            kafkaTemplate.send(KafkaTopics.PAYMENT_FAILED, event.orderId(),
                new PaymentFailedEvent(UUID.randomUUID().toString(), event.orderId(), "Simulated payment failure"));
        } else {
            String paymentId = UUID.randomUUID().toString();
            log.info("[CHOREOGRAPHY] PaymentConsumer: payment {} processed for order {}", paymentId, event.orderId());
            kafkaTemplate.send(KafkaTopics.PAYMENT_PROCESSED, event.orderId(),
                new PaymentProcessedEvent(paymentId, event.orderId(), event.amount(), event.simulateFailure(),
                    event.productId(), event.quantity()));
        }
    }

    // Compensation: cancel payment when inventory reservation fails
    @KafkaListener(topics = KafkaTopics.INVENTORY_FAILED, groupId = "payment-service-compensation")
    public void handleInventoryFailed(InventoryFailedEvent event) {
        log.info("[CHOREOGRAPHY] PaymentConsumer: compensating — cancelling payment {} for order {}", event.paymentId(), event.orderId());
        kafkaTemplate.send(KafkaTopics.PAYMENT_CANCELLED, event.orderId(),
            new PaymentCancelledEvent(event.paymentId(), event.orderId(), "Inventory failed: " + event.reason()));
    }
}

package io.housedevinci.sagapattern.choreography.consumer;

import io.housedevinci.sagapattern.choreography.config.KafkaTopics;
import io.housedevinci.sagapattern.choreography.event.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderConsumer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = KafkaTopics.ORDER_SHIPPED, groupId = "order-service")
    public void handleOrderShipped(OrderShippedEvent event) {
        log.info("[CHOREOGRAPHY] Order {} CONFIRMED — shipped via {}", event.orderId(), event.shipmentId());
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_FAILED, groupId = "order-service")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("[CHOREOGRAPHY] Order {} CANCELLED — payment failed: {}", event.orderId(), event.reason());
        kafkaTemplate.send(KafkaTopics.ORDER_CANCELLED, event.orderId(),
            new OrderCancelledEvent(event.orderId(), "Payment failed: " + event.reason()));
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_CANCELLED, groupId = "order-service")
    public void handlePaymentCancelled(PaymentCancelledEvent event) {
        log.info("[CHOREOGRAPHY] Order {} CANCELLED — {}", event.orderId(), event.reason());
        kafkaTemplate.send(KafkaTopics.ORDER_CANCELLED, event.orderId(),
            new OrderCancelledEvent(event.orderId(), event.reason()));
    }
}

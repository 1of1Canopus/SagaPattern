package io.housedevinci.sagapattern.choreography.producer;

import io.housedevinci.sagapattern.choreography.config.KafkaTopics;
import io.housedevinci.sagapattern.choreography.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventProducer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishOrderPlaced(OrderPlacedEvent event) {
        log.info("[CHOREOGRAPHY] Publishing OrderPlacedEvent for order {}", event.orderId());
        kafkaTemplate.send(KafkaTopics.ORDERS_PLACED, event.orderId(), event);
    }
}

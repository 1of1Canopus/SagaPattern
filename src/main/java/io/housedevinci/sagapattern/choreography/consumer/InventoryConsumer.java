package io.housedevinci.sagapattern.choreography.consumer;

import io.housedevinci.sagapattern.choreography.config.KafkaTopics;
import io.housedevinci.sagapattern.choreography.event.*;
import io.housedevinci.sagapattern.choreography.inventory.ProductStockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class InventoryConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryConsumer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductStockRepository stockRepository;

    public InventoryConsumer(KafkaTemplate<String, Object> kafkaTemplate,
                             ProductStockRepository stockRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.stockRepository = stockRepository;
    }

    @KafkaListener(topics = KafkaTopics.PAYMENT_PROCESSED, groupId = "inventory-service")
    @Transactional
    public void handlePaymentProcessed(PaymentProcessedEvent event) {
        log.info("[CHOREOGRAPHY] InventoryConsumer: reserving inventory for order {}", event.orderId());

        if ("INVENTORY".equals(event.simulateFailure())) {
            log.info("[CHOREOGRAPHY] InventoryConsumer: simulating failure for order {}", event.orderId());
            kafkaTemplate.send(KafkaTopics.INVENTORY_FAILED, event.orderId(),
                new InventoryFailedEvent(UUID.randomUUID().toString(), event.paymentId(), event.orderId(),
                    "Simulated inventory failure"));
            return;
        }

        var stock = stockRepository.findById(event.productId()).orElse(null);
        if (stock == null || !stock.reserve(event.quantity())) {
            String reason = stock == null ? "Product not found: " + event.productId()
                                          : "Insufficient stock for " + event.productId();
            log.warn("[CHOREOGRAPHY] InventoryConsumer: {} for order {}", reason, event.orderId());
            kafkaTemplate.send(KafkaTopics.INVENTORY_FAILED, event.orderId(),
                new InventoryFailedEvent(UUID.randomUUID().toString(), event.paymentId(), event.orderId(), reason));
            return;
        }

        stockRepository.save(stock);
        String inventoryId = UUID.randomUUID().toString();
        log.info("[CHOREOGRAPHY] InventoryConsumer: inventory {} reserved for order {} ({} x{})",
            inventoryId, event.orderId(), event.productId(), event.quantity());
        kafkaTemplate.send(KafkaTopics.INVENTORY_RESERVED, event.orderId(),
            new InventoryReservedEvent(inventoryId, event.orderId(), event.productId(), event.quantity()));
    }
}

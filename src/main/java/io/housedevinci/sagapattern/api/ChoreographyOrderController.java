package io.housedevinci.sagapattern.api;

import io.housedevinci.sagapattern.choreography.event.OrderPlacedEvent;
import io.housedevinci.sagapattern.choreography.producer.OrderEventProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/choreography/orders")
public class ChoreographyOrderController {

    private final OrderEventProducer producer;

    public ChoreographyOrderController(OrderEventProducer producer) {
        this.producer = producer;
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderPlacementRequest request) {
        String orderId = UUID.randomUUID().toString();
        producer.publishOrderPlaced(new OrderPlacedEvent(
            orderId,
            request.customerId(),
            request.productId(),
            request.quantity(),
            request.amount(),
            request.simulateFailure()
        ));
        return ResponseEntity.ok("Order " + orderId + " placed via choreography saga");
    }
}

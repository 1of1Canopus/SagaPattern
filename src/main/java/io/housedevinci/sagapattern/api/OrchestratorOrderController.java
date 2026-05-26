package io.housedevinci.sagapattern.api;

import io.housedevinci.sagapattern.orchestrator.command.PlaceOrderCommand;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/orchestrator/orders")
public class OrchestratorOrderController {

    private final CommandGateway commandGateway;

    public OrchestratorOrderController(CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    @PostMapping
    public ResponseEntity<String> placeOrder(@RequestBody OrderPlacementRequest request) {
        String orderId = UUID.randomUUID().toString();
        commandGateway.sendAndWait(new PlaceOrderCommand(
            orderId,
            request.customerId(),
            request.productId(),
            request.quantity(),
            request.amount(),
            request.simulateFailure()
        ));
        return ResponseEntity.ok("Order " + orderId + " placed via orchestrator saga");
    }
}

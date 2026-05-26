package io.housedevinci.sagapattern.orchestrator.saga;

import io.housedevinci.sagapattern.orchestrator.command.*;
import io.housedevinci.sagapattern.orchestrator.event.*;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Saga
public class OrderSaga {

    private static final Logger log = LoggerFactory.getLogger(OrderSaga.class);

    // transient — not persisted with saga state, re-injected on each activation
    @Autowired
    private transient CommandGateway commandGateway;

    private String orderId;
    private String productId;
    private int quantity;
    private double amount;
    private String simulateFailure;
    private String paymentId;
    private String inventoryId;

    @StartSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void on(OrderPlacedEvent event) {
        this.orderId = event.orderId();
        this.productId = event.productId();
        this.quantity = event.quantity();
        this.amount = event.amount();
        this.simulateFailure = event.simulateFailure();
        this.paymentId = UUID.randomUUID().toString();

        SagaLifecycle.associateWith("paymentId", paymentId);

        log.info("[ORCHESTRATOR SAGA] Order {} placed → ProcessPaymentCommand", orderId);
        commandGateway.send(new ProcessPaymentCommand(paymentId, orderId, amount, simulateFailure));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(PaymentProcessedEvent event) {
        this.inventoryId = UUID.randomUUID().toString();

        SagaLifecycle.associateWith("inventoryId", inventoryId);

        log.info("[ORCHESTRATOR SAGA] Payment {} processed → ReserveInventoryCommand", event.paymentId());
        commandGateway.send(new ReserveInventoryCommand(inventoryId, orderId, productId, quantity, simulateFailure));
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void on(PaymentFailedEvent event) {
        log.info("[ORCHESTRATOR SAGA] Payment failed → cancelling order {} [SAGA END]", orderId);
        commandGateway.send(new CancelOrderCommand(orderId, "Payment failed: " + event.reason()));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryReservedEvent event) {
        String shipmentId = UUID.randomUUID().toString();

        SagaLifecycle.associateWith("shipmentId", shipmentId);

        log.info("[ORCHESTRATOR SAGA] Inventory {} reserved → ShipOrderCommand", event.inventoryId());
        commandGateway.send(new ShipOrderCommand(shipmentId, orderId));
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void on(InventoryFailedEvent event) {
        log.info("[ORCHESTRATOR SAGA] Inventory failed → compensating payment {}", paymentId);
        commandGateway.send(new CancelPaymentCommand(paymentId, orderId, "Inventory failed: " + event.reason()));
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void on(PaymentCancelledEvent event) {
        log.info("[ORCHESTRATOR SAGA] Payment {} cancelled → cancelling order {} [SAGA END]", event.paymentId(), orderId);
        commandGateway.send(new CancelOrderCommand(orderId, "Compensated: inventory failure"));
    }

    @SagaEventHandler(associationProperty = "orderId")
    @EndSaga
    public void on(OrderShippedEvent event) {
        log.info("[ORCHESTRATOR SAGA] Order {} shipped — COMPLETE [SAGA END]", orderId);
    }
}

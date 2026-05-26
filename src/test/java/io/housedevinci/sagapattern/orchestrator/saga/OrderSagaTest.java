package io.housedevinci.sagapattern.orchestrator.saga;

import io.housedevinci.sagapattern.orchestrator.command.*;
import io.housedevinci.sagapattern.orchestrator.event.*;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.axonframework.test.matchers.Matchers.payloadsMatching;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;

class OrderSagaTest {

    private SagaTestFixture<OrderSaga> fixture;

    @BeforeEach
    void setUp() {
        fixture = new SagaTestFixture<>(OrderSaga.class);
    }

    @Test
    void orderPlaced_shouldDispatchProcessPaymentCommand() {
        fixture.givenNoPriorActivity()
            .whenPublishingA(new OrderPlacedEvent("order-1", "c1", "p1", 1, 50.0, null))
            .expectDispatchedCommandsMatching(payloadsMatching(hasItem(instanceOf(ProcessPaymentCommand.class))));
    }

    @Test
    void paymentProcessed_shouldDispatchReserveInventoryCommand() {
        fixture.givenAPublished(new OrderPlacedEvent("order-1", "c1", "p1", 1, 50.0, null))
            .whenPublishingA(new PaymentProcessedEvent("pay-1", "order-1", 50.0))
            .expectDispatchedCommandsMatching(payloadsMatching(hasItem(instanceOf(ReserveInventoryCommand.class))));
    }

    @Test
    void paymentFailed_shouldCancelOrderAndEndSaga() {
        fixture.givenAPublished(new OrderPlacedEvent("order-1", "c1", "p1", 1, 50.0, "PAYMENT"))
            .whenPublishingA(new PaymentFailedEvent("pay-1", "order-1", "Simulated"))
            .expectDispatchedCommandsMatching(payloadsMatching(hasItem(instanceOf(CancelOrderCommand.class))))
            .expectActiveSagas(0);
    }

    @Test
    void inventoryReserved_shouldDispatchShipOrderCommand() {
        fixture.givenAPublished(new OrderPlacedEvent("order-1", "c1", "p1", 1, 50.0, null))
            .andThenAPublished(new PaymentProcessedEvent("pay-1", "order-1", 50.0))
            .whenPublishingA(new InventoryReservedEvent("inv-1", "order-1", "p1", 1))
            .expectDispatchedCommandsMatching(payloadsMatching(hasItem(instanceOf(ShipOrderCommand.class))));
    }

    @Test
    void inventoryFailed_shouldDispatchCancelPaymentCommand() {
        fixture.givenAPublished(new OrderPlacedEvent("order-1", "c1", "p1", 1, 50.0, "INVENTORY"))
            .andThenAPublished(new PaymentProcessedEvent("pay-1", "order-1", 50.0))
            .whenPublishingA(new InventoryFailedEvent("inv-1", "order-1", "Simulated"))
            .expectDispatchedCommandsMatching(payloadsMatching(hasItem(instanceOf(CancelPaymentCommand.class))));
    }

    @Test
    void paymentCancelled_shouldCancelOrderAndEndSaga() {
        fixture.givenAPublished(new OrderPlacedEvent("order-1", "c1", "p1", 1, 50.0, "INVENTORY"))
            .andThenAPublished(new PaymentProcessedEvent("pay-1", "order-1", 50.0))
            .andThenAPublished(new InventoryFailedEvent("inv-1", "order-1", "Simulated"))
            .whenPublishingA(new PaymentCancelledEvent("pay-1", "order-1", "Inventory failed"))
            .expectDispatchedCommandsMatching(payloadsMatching(hasItem(instanceOf(CancelOrderCommand.class))))
            .expectActiveSagas(0);
    }

    @Test
    void orderShipped_shouldEndSaga() {
        fixture.givenAPublished(new OrderPlacedEvent("order-1", "c1", "p1", 1, 50.0, null))
            .andThenAPublished(new PaymentProcessedEvent("pay-1", "order-1", 50.0))
            .andThenAPublished(new InventoryReservedEvent("inv-1", "order-1", "p1", 1))
            .whenPublishingA(new OrderShippedEvent("ship-1", "order-1"))
            .expectActiveSagas(0);
    }
}

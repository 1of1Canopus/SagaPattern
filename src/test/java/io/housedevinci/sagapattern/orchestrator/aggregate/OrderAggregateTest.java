package io.housedevinci.sagapattern.orchestrator.aggregate;

import io.housedevinci.sagapattern.orchestrator.command.CancelOrderCommand;
import io.housedevinci.sagapattern.orchestrator.command.PlaceOrderCommand;
import io.housedevinci.sagapattern.orchestrator.event.OrderCancelledEvent;
import io.housedevinci.sagapattern.orchestrator.event.OrderPlacedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderAggregateTest {

    private AggregateTestFixture<OrderAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(OrderAggregate.class);
    }

    @Test
    void placeOrder_shouldEmitOrderPlacedEvent() {
        fixture.givenNoPriorActivity()
            .when(new PlaceOrderCommand("order-1", "customer-1", "product-1", 2, 99.99, null))
            .expectEvents(new OrderPlacedEvent("order-1", "customer-1", "product-1", 2, 99.99, null));
    }

    @Test
    void cancelOrder_shouldEmitOrderCancelledEvent() {
        fixture.given(new OrderPlacedEvent("order-1", "customer-1", "product-1", 2, 99.99, null))
            .when(new CancelOrderCommand("order-1", "Payment failed"))
            .expectEvents(new OrderCancelledEvent("order-1", "Payment failed"));
    }
}

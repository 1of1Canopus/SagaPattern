package io.housedevinci.sagapattern.orchestrator.aggregate;

import io.housedevinci.sagapattern.orchestrator.command.ShipOrderCommand;
import io.housedevinci.sagapattern.orchestrator.event.OrderShippedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ShipmentAggregateTest {

    private AggregateTestFixture<ShipmentAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(ShipmentAggregate.class);
    }

    @Test
    void shipOrder_shouldEmitOrderShippedEvent() {
        fixture.givenNoPriorActivity()
            .when(new ShipOrderCommand("shipment-1", "order-1"))
            .expectEvents(new OrderShippedEvent("shipment-1", "order-1"));
    }
}

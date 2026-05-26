package io.housedevinci.sagapattern.orchestrator.aggregate;

import io.housedevinci.sagapattern.orchestrator.command.ReserveInventoryCommand;
import io.housedevinci.sagapattern.orchestrator.event.InventoryFailedEvent;
import io.housedevinci.sagapattern.orchestrator.event.InventoryReservedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryAggregateTest {

    private AggregateTestFixture<InventoryAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(InventoryAggregate.class);
    }

    @Test
    void reserveInventory_happyPath_shouldEmitInventoryReservedEvent() {
        fixture.givenNoPriorActivity()
            .when(new ReserveInventoryCommand("inv-1", "order-1", "product-1", 2, null))
            .expectEvents(new InventoryReservedEvent("inv-1", "order-1", "product-1", 2));
    }

    @Test
    void reserveInventory_withSimulateFailure_shouldEmitInventoryFailedEvent() {
        fixture.givenNoPriorActivity()
            .when(new ReserveInventoryCommand("inv-1", "order-1", "product-1", 2, "INVENTORY"))
            .expectEvents(new InventoryFailedEvent("inv-1", "order-1", "Simulated inventory failure"));
    }
}

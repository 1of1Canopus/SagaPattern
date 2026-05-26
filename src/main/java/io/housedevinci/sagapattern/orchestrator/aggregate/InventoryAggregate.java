package io.housedevinci.sagapattern.orchestrator.aggregate;

import io.housedevinci.sagapattern.orchestrator.command.ReserveInventoryCommand;
import io.housedevinci.sagapattern.orchestrator.event.InventoryFailedEvent;
import io.housedevinci.sagapattern.orchestrator.event.InventoryReservedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aggregate
public class InventoryAggregate {

    private static final Logger log = LoggerFactory.getLogger(InventoryAggregate.class);

    @AggregateIdentifier
    private String inventoryId;
    private String state;

    @CommandHandler
    public InventoryAggregate(ReserveInventoryCommand command) {
        if ("INVENTORY".equals(command.simulateFailure())) {
            log.info("[ORCHESTRATOR] Simulating inventory failure for order {}", command.orderId());
            AggregateLifecycle.apply(new InventoryFailedEvent(
                command.inventoryId(), command.orderId(), "Simulated inventory failure"
            ));
        } else {
            log.info("[ORCHESTRATOR] Reserving inventory {} for order {}", command.inventoryId(), command.orderId());
            AggregateLifecycle.apply(new InventoryReservedEvent(
                command.inventoryId(), command.orderId(), command.productId(), command.quantity()
            ));
        }
    }

    protected InventoryAggregate() {}

    @EventSourcingHandler
    public void on(InventoryReservedEvent event) {
        this.inventoryId = event.inventoryId();
        this.state = "RESERVED";
    }

    @EventSourcingHandler
    public void on(InventoryFailedEvent event) {
        this.inventoryId = event.inventoryId();
        this.state = "FAILED";
    }
}

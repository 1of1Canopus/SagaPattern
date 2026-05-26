package io.housedevinci.sagapattern.orchestrator.aggregate;

import io.housedevinci.sagapattern.orchestrator.command.ShipOrderCommand;
import io.housedevinci.sagapattern.orchestrator.event.OrderShippedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aggregate
public class ShipmentAggregate {

    private static final Logger log = LoggerFactory.getLogger(ShipmentAggregate.class);

    @AggregateIdentifier
    private String shipmentId;
    private String state;

    @CommandHandler
    public ShipmentAggregate(ShipOrderCommand command) {
        log.info("[ORCHESTRATOR] Shipping order {} via shipment {}", command.orderId(), command.shipmentId());
        AggregateLifecycle.apply(new OrderShippedEvent(command.shipmentId(), command.orderId()));
    }

    protected ShipmentAggregate() {}

    @EventSourcingHandler
    public void on(OrderShippedEvent event) {
        this.shipmentId = event.shipmentId();
        this.state = "SHIPPED";
    }
}

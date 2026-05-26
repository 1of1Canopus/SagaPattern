package io.housedevinci.sagapattern.orchestrator.aggregate;

import io.housedevinci.sagapattern.orchestrator.command.CancelOrderCommand;
import io.housedevinci.sagapattern.orchestrator.command.PlaceOrderCommand;
import io.housedevinci.sagapattern.orchestrator.event.OrderCancelledEvent;
import io.housedevinci.sagapattern.orchestrator.event.OrderPlacedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aggregate
public class OrderAggregate {

    private static final Logger log = LoggerFactory.getLogger(OrderAggregate.class);

    @AggregateIdentifier
    private String orderId;
    private String state;

    @CommandHandler
    public OrderAggregate(PlaceOrderCommand command) {
        log.info("[ORCHESTRATOR] Creating order {}", command.orderId());
        AggregateLifecycle.apply(new OrderPlacedEvent(
            command.orderId(), command.customerId(), command.productId(),
            command.quantity(), command.amount(), command.simulateFailure()
        ));
    }

    protected OrderAggregate() {}

    @CommandHandler
    public void handle(CancelOrderCommand command) {
        log.info("[ORCHESTRATOR] Cancelling order {} — reason: {}", orderId, command.reason());
        AggregateLifecycle.apply(new OrderCancelledEvent(orderId, command.reason()));
    }

    @EventSourcingHandler
    public void on(OrderPlacedEvent event) {
        this.orderId = event.orderId();
        this.state = "PENDING";
    }

    @EventSourcingHandler
    public void on(OrderCancelledEvent event) {
        this.state = "CANCELLED";
    }
}

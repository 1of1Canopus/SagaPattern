package io.housedevinci.sagapattern.orchestrator.aggregate;

import io.housedevinci.sagapattern.orchestrator.command.CancelPaymentCommand;
import io.housedevinci.sagapattern.orchestrator.command.ProcessPaymentCommand;
import io.housedevinci.sagapattern.orchestrator.event.PaymentCancelledEvent;
import io.housedevinci.sagapattern.orchestrator.event.PaymentFailedEvent;
import io.housedevinci.sagapattern.orchestrator.event.PaymentProcessedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aggregate
public class PaymentAggregate {

    private static final Logger log = LoggerFactory.getLogger(PaymentAggregate.class);

    @AggregateIdentifier
    private String paymentId;
    private String state;

    @CommandHandler
    public PaymentAggregate(ProcessPaymentCommand command) {
        if ("PAYMENT".equals(command.simulateFailure())) {
            log.info("[ORCHESTRATOR] Simulating payment failure for order {}", command.orderId());
            AggregateLifecycle.apply(new PaymentFailedEvent(
                command.paymentId(), command.orderId(), "Simulated payment failure"
            ));
        } else {
            log.info("[ORCHESTRATOR] Processing payment {} for order {}", command.paymentId(), command.orderId());
            AggregateLifecycle.apply(new PaymentProcessedEvent(
                command.paymentId(), command.orderId(), command.amount()
            ));
        }
    }

    protected PaymentAggregate() {}

    @CommandHandler
    public void handle(CancelPaymentCommand command) {
        log.info("[ORCHESTRATOR] Cancelling payment {} — reason: {}", paymentId, command.reason());
        AggregateLifecycle.apply(new PaymentCancelledEvent(paymentId, command.orderId(), command.reason()));
    }

    @EventSourcingHandler
    public void on(PaymentProcessedEvent event) {
        this.paymentId = event.paymentId();
        this.state = "PROCESSED";
    }

    @EventSourcingHandler
    public void on(PaymentFailedEvent event) {
        this.paymentId = event.paymentId();
        this.state = "FAILED";
    }

    @EventSourcingHandler
    public void on(PaymentCancelledEvent event) {
        this.state = "CANCELLED";
    }
}

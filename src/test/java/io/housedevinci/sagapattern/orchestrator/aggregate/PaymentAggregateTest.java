package io.housedevinci.sagapattern.orchestrator.aggregate;

import io.housedevinci.sagapattern.orchestrator.command.CancelPaymentCommand;
import io.housedevinci.sagapattern.orchestrator.command.ProcessPaymentCommand;
import io.housedevinci.sagapattern.orchestrator.event.PaymentCancelledEvent;
import io.housedevinci.sagapattern.orchestrator.event.PaymentFailedEvent;
import io.housedevinci.sagapattern.orchestrator.event.PaymentProcessedEvent;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaymentAggregateTest {

    private AggregateTestFixture<PaymentAggregate> fixture;

    @BeforeEach
    void setUp() {
        fixture = new AggregateTestFixture<>(PaymentAggregate.class);
    }

    @Test
    void processPayment_happyPath_shouldEmitPaymentProcessedEvent() {
        fixture.givenNoPriorActivity()
            .when(new ProcessPaymentCommand("payment-1", "order-1", 99.99, null))
            .expectEvents(new PaymentProcessedEvent("payment-1", "order-1", 99.99));
    }

    @Test
    void processPayment_withSimulateFailure_shouldEmitPaymentFailedEvent() {
        fixture.givenNoPriorActivity()
            .when(new ProcessPaymentCommand("payment-1", "order-1", 99.99, "PAYMENT"))
            .expectEvents(new PaymentFailedEvent("payment-1", "order-1", "Simulated payment failure"));
    }

    @Test
    void cancelPayment_shouldEmitPaymentCancelledEvent() {
        fixture.given(new PaymentProcessedEvent("payment-1", "order-1", 99.99))
            .when(new CancelPaymentCommand("payment-1", "order-1", "Inventory failed"))
            .expectEvents(new PaymentCancelledEvent("payment-1", "order-1", "Inventory failed"));
    }
}

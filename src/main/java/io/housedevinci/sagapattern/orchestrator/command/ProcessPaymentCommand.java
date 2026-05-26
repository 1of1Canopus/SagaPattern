package io.housedevinci.sagapattern.orchestrator.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record ProcessPaymentCommand(
    @TargetAggregateIdentifier String paymentId,
    String orderId,
    double amount,
    String simulateFailure
) {}

package io.housedevinci.sagapattern.orchestrator.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CancelPaymentCommand(
    @TargetAggregateIdentifier String paymentId,
    String orderId,
    String reason
) {}

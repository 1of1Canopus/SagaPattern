package io.housedevinci.sagapattern.orchestrator.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record CancelOrderCommand(
    @TargetAggregateIdentifier String orderId,
    String reason
) {}

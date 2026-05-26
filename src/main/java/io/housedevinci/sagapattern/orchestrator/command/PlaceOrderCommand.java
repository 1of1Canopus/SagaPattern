package io.housedevinci.sagapattern.orchestrator.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record PlaceOrderCommand(
    @TargetAggregateIdentifier String orderId,
    String customerId,
    String productId,
    int quantity,
    double amount,
    String simulateFailure
) {}

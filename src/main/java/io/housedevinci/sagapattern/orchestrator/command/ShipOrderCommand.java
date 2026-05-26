package io.housedevinci.sagapattern.orchestrator.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record ShipOrderCommand(
    @TargetAggregateIdentifier String shipmentId,
    String orderId
) {}

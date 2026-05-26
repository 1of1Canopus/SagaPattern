package io.housedevinci.sagapattern.orchestrator.command;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

public record ReserveInventoryCommand(
    @TargetAggregateIdentifier String inventoryId,
    String orderId,
    String productId,
    int quantity,
    String simulateFailure
) {}

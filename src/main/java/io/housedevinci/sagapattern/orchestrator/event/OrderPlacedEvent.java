package io.housedevinci.sagapattern.orchestrator.event;

public record OrderPlacedEvent(
    String orderId,
    String customerId,
    String productId,
    int quantity,
    double amount,
    String simulateFailure
) {}

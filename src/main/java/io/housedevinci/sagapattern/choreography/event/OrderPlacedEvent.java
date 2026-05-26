package io.housedevinci.sagapattern.choreography.event;

public record OrderPlacedEvent(
    String orderId,
    String customerId,
    String productId,
    int quantity,
    double amount,
    String simulateFailure
) {}

package io.housedevinci.sagapattern.api;

public record OrderPlacementRequest(
    String customerId,
    String productId,
    int quantity,
    double amount,
    String simulateFailure
) {}

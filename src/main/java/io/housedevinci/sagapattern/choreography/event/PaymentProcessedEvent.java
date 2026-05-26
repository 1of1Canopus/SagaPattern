package io.housedevinci.sagapattern.choreography.event;

public record PaymentProcessedEvent(
    String paymentId,
    String orderId,
    double amount,
    String simulateFailure
) {}

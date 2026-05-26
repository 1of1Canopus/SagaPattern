package io.housedevinci.sagapattern.orchestrator.event;

public record PaymentProcessedEvent(
    String paymentId,
    String orderId,
    double amount
) {}

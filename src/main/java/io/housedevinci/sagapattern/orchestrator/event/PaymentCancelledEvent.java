package io.housedevinci.sagapattern.orchestrator.event;

public record PaymentCancelledEvent(
    String paymentId,
    String orderId,
    String reason
) {}

package io.housedevinci.sagapattern.orchestrator.event;

public record PaymentFailedEvent(
    String paymentId,
    String orderId,
    String reason
) {}

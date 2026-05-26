package io.housedevinci.sagapattern.choreography.event;

public record PaymentFailedEvent(
    String paymentId,
    String orderId,
    String reason
) {}

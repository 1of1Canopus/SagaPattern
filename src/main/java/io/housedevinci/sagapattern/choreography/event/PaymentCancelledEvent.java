package io.housedevinci.sagapattern.choreography.event;

public record PaymentCancelledEvent(
    String paymentId,
    String orderId,
    String reason
) {}

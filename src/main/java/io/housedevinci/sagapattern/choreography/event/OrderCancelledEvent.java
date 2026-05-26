package io.housedevinci.sagapattern.choreography.event;

public record OrderCancelledEvent(
    String orderId,
    String reason
) {}

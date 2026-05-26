package io.housedevinci.sagapattern.orchestrator.event;

public record OrderCancelledEvent(
    String orderId,
    String reason
) {}

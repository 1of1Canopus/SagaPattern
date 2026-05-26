package io.housedevinci.sagapattern.orchestrator.event;

public record OrderShippedEvent(
    String shipmentId,
    String orderId
) {}

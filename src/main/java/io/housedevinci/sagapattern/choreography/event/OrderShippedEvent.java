package io.housedevinci.sagapattern.choreography.event;

public record OrderShippedEvent(
    String shipmentId,
    String orderId
) {}

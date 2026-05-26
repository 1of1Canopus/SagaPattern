package io.housedevinci.sagapattern.choreography.event;

public record InventoryFailedEvent(
    String inventoryId,
    String paymentId,
    String orderId,
    String reason
) {}

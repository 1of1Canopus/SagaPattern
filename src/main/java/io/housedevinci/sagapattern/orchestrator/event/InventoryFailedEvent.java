package io.housedevinci.sagapattern.orchestrator.event;

public record InventoryFailedEvent(
    String inventoryId,
    String orderId,
    String reason
) {}

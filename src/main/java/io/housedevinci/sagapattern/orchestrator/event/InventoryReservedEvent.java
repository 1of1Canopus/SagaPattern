package io.housedevinci.sagapattern.orchestrator.event;

public record InventoryReservedEvent(
    String inventoryId,
    String orderId,
    String productId,
    int quantity
) {}

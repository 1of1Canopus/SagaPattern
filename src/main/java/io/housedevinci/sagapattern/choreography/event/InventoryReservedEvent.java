package io.housedevinci.sagapattern.choreography.event;

public record InventoryReservedEvent(
    String inventoryId,
    String orderId,
    String productId,
    int quantity
) {}

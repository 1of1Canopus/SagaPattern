package io.housedevinci.sagapattern.choreography.config;

public final class KafkaTopics {
    public static final String ORDERS_PLACED      = "saga.orders.placed";
    public static final String PAYMENT_PROCESSED  = "saga.payment.processed";
    public static final String PAYMENT_FAILED     = "saga.payment.failed";
    public static final String PAYMENT_CANCELLED  = "saga.payment.cancelled";
    public static final String INVENTORY_RESERVED = "saga.inventory.reserved";
    public static final String INVENTORY_FAILED   = "saga.inventory.failed";
    public static final String ORDER_SHIPPED      = "saga.order.shipped";
    public static final String ORDER_CANCELLED    = "saga.order.cancelled";

    private KafkaTopics() {}
}

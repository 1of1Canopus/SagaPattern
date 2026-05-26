package io.housedevinci.sagapattern.choreography.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean public NewTopic ordersPlaced()      { return TopicBuilder.name(KafkaTopics.ORDERS_PLACED).build(); }
    @Bean public NewTopic paymentProcessed()  { return TopicBuilder.name(KafkaTopics.PAYMENT_PROCESSED).build(); }
    @Bean public NewTopic paymentFailed()     { return TopicBuilder.name(KafkaTopics.PAYMENT_FAILED).build(); }
    @Bean public NewTopic paymentCancelled()  { return TopicBuilder.name(KafkaTopics.PAYMENT_CANCELLED).build(); }
    @Bean public NewTopic inventoryReserved() { return TopicBuilder.name(KafkaTopics.INVENTORY_RESERVED).build(); }
    @Bean public NewTopic inventoryFailed()   { return TopicBuilder.name(KafkaTopics.INVENTORY_FAILED).build(); }
    @Bean public NewTopic orderShipped()      { return TopicBuilder.name(KafkaTopics.ORDER_SHIPPED).build(); }
    @Bean public NewTopic orderCancelled()    { return TopicBuilder.name(KafkaTopics.ORDER_CANCELLED).build(); }
}

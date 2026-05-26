package io.housedevinci.sagapattern.config;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.inmemory.InMemoryEventStorageEngine;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class AxonTestConfig {

    @Bean
    @Primary
    public EventStorageEngine eventStorageEngine() {
        return new InMemoryEventStorageEngine();
    }

    @Bean
    @Primary
    public EventStore eventStore(EventStorageEngine engine) {
        return EmbeddedEventStore.builder().storageEngine(engine).build();
    }

    @Bean
    @Primary
    public CommandBus commandBus() {
        return SimpleCommandBus.builder().build();
    }

    @Bean
    @Primary
    public CommandGateway commandGateway(CommandBus commandBus) {
        return DefaultCommandGateway.builder().commandBus(commandBus).build();
    }
}

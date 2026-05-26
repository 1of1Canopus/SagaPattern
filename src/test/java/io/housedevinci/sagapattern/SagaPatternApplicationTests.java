package io.housedevinci.sagapattern;

import io.housedevinci.sagapattern.config.AxonTestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@EmbeddedKafka(partitions = 1)
@TestPropertySource(properties = {
    "axon.axonserver.enabled=false",
    "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
@Import(AxonTestConfig.class)
class SagaPatternApplicationTests {

    @Test
    void contextLoads() {}
}

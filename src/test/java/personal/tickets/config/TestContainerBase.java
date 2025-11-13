package personal.tickets.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestContainerBase {

    @LocalServerPort
    protected int port;

    @TestConfiguration
    static class ContainerConfiguration {

        // 1. MySQL 컨테이너 (ServiceConnection 자동 설정 활용)
        @Bean
        @ServiceConnection
        // JDBC URL, Username, Password를 자동으로 설정
        MySQLContainer<?> mySQLContainer() {
            return new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
                    .withDatabaseName("testdb")
                    .withUsername("testuser")
                    .withPassword("testpass");
        }

        // 2. Redis 컨테이너 (ServiceConnection 자동 설정 활용)
        @Bean
        @ServiceConnection(name = "redis")
        // Host와 Port를 자동으로 설정
        GenericContainer<?> redisContainer() {
            return new GenericContainer<>(DockerImageName.parse("redis:6.2.6"))
                    .withExposedPorts(6379);
        }

        // 3. Kafka 컨테이너 (수동 설정 필요)
        static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
                DockerImageName.parse("confluentinc/cp-kafka:7.4.0")
        );

        // 정적 블록에서 Kafka 컨테이너를 시작 (JUnit 라이프사이클 밖에서 관리)
        static {
            KAFKA_CONTAINER.start();
        }

        /**
         * Kafka의 브로커 주소를 Spring Boot 설정에 동적으로 주입합니다.
         */
        @DynamicPropertySource
        static void kafkaProperties(DynamicPropertyRegistry registry) {
            // Spring Boot의 'spring.kafka.bootstrap-servers' 설정 오버라이드
            registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        }
    }
}
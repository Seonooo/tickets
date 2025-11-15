package personal.tickets.config;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
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
            return new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
                    .withExposedPorts(6379);
        }

        // 3. Kafka 컨테이너
        @Bean(initMethod = "start", destroyMethod = "stop")
        KafkaContainer kafkaContainer() { // 파라미터에서 DynamicPropertyRegistry 제거!
            return new KafkaContainer(
                    DockerImageName.parse("confluentinc/cp-kafka:7.5.0")
            );
        }

        // 4. DynamicPropertyRegistrar Bean 추가 (새로운 방식)
        @Bean
        public DynamicPropertyRegistrar kafkaPropertiesRegistrar(KafkaContainer kafkaContainer) {
            return registry -> registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        }
    }
}
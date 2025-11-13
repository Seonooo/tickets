package personal.tickets.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import personal.tickets.config.TestContainerBase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HealthCheckSteps extends TestContainerBase {

    @Autowired
    private TestRestTemplate restTemplate;

    private ResponseEntity<String> responseEntity;

    @When("클라이언트가 API를 요청할 때")
    public void client_requests_api() {
        // TestContainerBase에서 주입받은 동적 포트(port) 사용
        String endpoint = "/health";
        String url = "http://localhost:" + port + endpoint;
        responseEntity = restTemplate.getForEntity(url, String.class);
    }

    @Then("응답 상태 코드는 {int} 이어야 한다")
    public void response_status_code_should_be(int expectedStatusCode) {
        assertEquals(expectedStatusCode, responseEntity.getStatusCode().value(), "상태 코드가 일치하지 않습니다.");
    }

    @Then("응답 본문은 {string}이어야 한다")
    public void response_body_should_be(String expectedBody) {

        // 여기서는 가장 일반적인 UP/DOWN 상태 체크로 로직을 작성합니다.
        String body = responseEntity.getBody();
        assertTrue(body != null && body.contains(expectedBody), "응답 본문이 'OK' 또는 'UP'을 포함하지 않습니다.");
    }
}

package tech.ioco.review.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.assertj.core.api.Assertions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HomeControllerTests {
    TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void testHomeControllerGetRequest() {
        ResponseEntity<Void> response = restTemplate
                .getForEntity("http://localhost:8080/", Void.class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}

package tech.ioco.review.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import tech.ioco.review.entity.Team;

import java.util.UUID;

import org.assertj.core.api.Assertions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TeamControllerTests {

    TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    void getRequestTest() {
        ResponseEntity<Team[]> response = restTemplate
                .getForEntity("http://localhost:8080/groups",
                        Team[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void postRequestTest() {
        Team newGroup = new Team(UUID.randomUUID(), "group test", false);
        ResponseEntity<Void> response = restTemplate
                .postForEntity("http://localhost:8080/groups", newGroup, Void.class);
        System.out.println(response.getHeaders());
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}

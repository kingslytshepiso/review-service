package tech.ioco.review.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import tech.ioco.review.data.TeamRepository;
import tech.ioco.review.entity.Team;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TeamControllerTests {

    TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    TeamRepository repo;

    private final String testUrl = "http://localhost:8080/teams";

    List<Team> availableTeams;

    @BeforeEach
    public void setUp() {
        availableTeams = repo.findAll();
    }

    @Test
    void getRequestTest() {
        ResponseEntity<Team[]> response = restTemplate
                .getForEntity(testUrl,
                        Team[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void postRequestTest() {
        UUID newId = UUID.randomUUID();
        Team newGroup = new Team(newId, "team test", true);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity(testUrl, newGroup, Void.class);
        Assertions.assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        if (postResponse.getStatusCode().is2xxSuccessful()) {
            availableTeams = repo.findAll();
            ResponseEntity<Team> getResponse = restTemplate
                    .getForEntity(postResponse.getHeaders().getLocation().toString(), Team.class);
            Assertions.assertThat(availableTeams).contains(getResponse.getBody());
        }

    }

    @Test
    void postRequestWithConflictTest() {
        //Test only if there are records in the database
        if (!availableTeams.isEmpty()) {
            Team newTeam = new Team(
                    availableTeams.getFirst().getId(),
                    availableTeams.getFirst().getName(),
                    availableTeams.getFirst().isReviewer()
            );
            ResponseEntity<Void> postResponse = restTemplate
                    .postForEntity(testUrl, newTeam, Void.class);
            Assertions.assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        }
    }

    @Test
    void getTeamWithIdRequestTest() {
        if (!availableTeams.isEmpty()) {
            ResponseEntity<Team> response = restTemplate.getForEntity(
                    testUrl + "/" +
                            availableTeams.getFirst().getId(), Team.class
            );
            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }
    }

    @Test
    void getTeamWithIdRequestFailureTest() {
        if (!availableTeams.isEmpty()) {
            ResponseEntity<Team> response = restTemplate
                    .getForEntity(testUrl + "/" + UUID.randomUUID(), Team.class);
            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    void updateRequestTest() {
        if (!availableTeams.isEmpty()) {
            Team toUpdate = availableTeams.getLast();
            Team updated = new Team(
                    toUpdate.getId(),
                    toUpdate.getName() + 0,
                    true
            );
            HttpEntity<Team> httpEntity = new HttpEntity<Team>(updated);
            ResponseEntity<Void> response = restTemplate
                    .exchange(URI.create(testUrl + "/" + toUpdate.getId()), HttpMethod.PUT, httpEntity, Void.class);
            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            availableTeams = repo.findAll();
            Assertions.assertThat(availableTeams.getLast().getId()).isEqualTo(updated.getId());
            Assertions.assertThat(availableTeams.getLast().getName()).isEqualTo(updated.getName());
            Assertions.assertThat(availableTeams.getLast().isReviewer()).isEqualTo(updated.isReviewer());
        }
    }

    @Test
    void updateRequestFailureTest() {
        if (!availableTeams.isEmpty()) {
            Team toUpdate = availableTeams.getLast();
            Team updated = new Team(
                    null,
                    toUpdate.getName() + 0,
                    true
            );
            HttpEntity<Team> httpEntity = new HttpEntity<Team>(updated);
            ResponseEntity<Void> response = restTemplate
                    .exchange(URI.create(testUrl + "/" + UUID.randomUUID()), HttpMethod.PUT, httpEntity, Void.class);
            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    void deleteRequestTest() {
        if (!availableTeams.isEmpty()) {
            Team toDelete = availableTeams.getLast();
            ResponseEntity<Void> deleteResponse = restTemplate
                    .exchange(URI.create(testUrl + "/" + toDelete.getId()), HttpMethod.DELETE, null, Void.class);
            Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            ResponseEntity<Team> getResponse = restTemplate
                    .getForEntity(testUrl + "/" + toDelete.getId(), Team.class);
            Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }
}

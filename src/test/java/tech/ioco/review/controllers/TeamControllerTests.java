package tech.ioco.review.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.test.annotation.DirtiesContext;
import tech.ioco.review.data.TeamRepository;
import tech.ioco.review.entity.Team;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.assertj.core.api.Assertions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Order(1)
    void getRequestTest() {
        ResponseEntity<Team[]> response = restTemplate
                .getForEntity(testUrl,
                        Team[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(2)
    void postRequestTest() {
        Team newGroup = new Team(null,
                "Dev#test team 1",
                true);
        ResponseEntity<Void> postResponse = restTemplate
                .postForEntity(testUrl, newGroup, Void.class);
        Assertions.assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        if (postResponse.getStatusCode().is2xxSuccessful()) {
            ResponseEntity<Team> getResponse = restTemplate
                    .getForEntity(postResponse.getHeaders().getLocation().toString(), Team.class);
            ResponseEntity<Team[]> getAllResponse = restTemplate
                    .getForEntity(testUrl, Team[].class);
            Assertions.assertThat(getAllResponse.getBody()).contains(getResponse.getBody());
        }

    }

    @Test
    @Order(3)
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
    @Order(4)
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
    @Order(4)
    void getTeamWithIdRequestFailureTest() {
        if (!availableTeams.isEmpty()) {
            ResponseEntity<Team> response = restTemplate
                    .getForEntity(testUrl + "/" + UUID.randomUUID(), Team.class);
            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    @Order(5)
    void updateRequestTest() {
        if (!availableTeams.isEmpty()) {
            Team toUpdate = availableTeams.getLast();
            Team updated = new Team(
                    toUpdate.getId(),
                    "Dev#test team 1 updated",
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
    @Order(6)
    void updateTeamThatDoesNotExistFailureTest() {
        if (!availableTeams.isEmpty()) {
            Team toUpdate = availableTeams.getLast();
            Team updated = new Team(
                    null,
                    "Dev#test team 1 updated",
                    true
            );
            HttpEntity<Team> httpEntity = new HttpEntity<Team>(updated);
            ResponseEntity<Void> response = restTemplate
                    .exchange(URI.create(testUrl + "/" + UUID.randomUUID()), HttpMethod.PUT, httpEntity, Void.class);
            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    @Order(7)
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

    @AfterAll
    public void conclude() {
        repo.deleteAll(repo.findAllByNameStartingWith("Dev#test"));
    }
}

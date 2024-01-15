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
import tech.ioco.review.repository.MemberRepository;
import tech.ioco.review.repository.RoleRepository;
import tech.ioco.review.repository.TeamRepository;
import tech.ioco.review.entity.Member;
import tech.ioco.review.entity.Role;
import tech.ioco.review.entity.Team;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
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
    TeamRepository teamRepo;
    @Autowired
    MemberRepository memberRepo;

    @Autowired
    RoleRepository roleRepo;

    private final String testUrl = "http://localhost:8080/teams";
    private Role testRole;
    private List<Member> testMembers;

    List<Team> availableTeams;

    @BeforeAll
    public void setUp() {
        testRole = roleRepo.save(new Role(null, "Dev#test 'Solution Architect'"));
        List<Member> members = new ArrayList<Member>();
        members.add(new Member(
                null,
                "Dev#test member 1 name",
                "Dev#test member 1 surname",
                "Dev#test member 1 email"
        ));
        members.add(new Member(
                null,
                "Dev#test member 2 name",
                "Dev#test member 2 surname",
                "Dev#test member 2 email"
        ));
        testMembers = new ArrayList<>();
        members.forEach(member -> {
            member.setRole(testRole);
            testMembers.add(member);
        });
        testMembers = memberRepo.saveAll(testMembers);
    }

    @BeforeEach
    public void setForEach() {
        availableTeams = teamRepo.findAll();
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
    @Order(2)
    void postATeamWithMembersTest() {
        Team newGroup = new Team(null,
                "Dev#test team 2",
                true);
        newGroup.setMembers(new HashSet<>(testMembers));
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
    @Order(2)
    void postATeamWithNonExistentTeamMembers() {
        Team newGroup = new Team(null,
                "Dev#test team 7",
                true);
        List<Member> nonExistentMembers = new ArrayList<>();
        nonExistentMembers.add(new Member(
                null,
                "Dev#test non-existent member name",
                "Dev#test non-existent member surname",
                "Dev#test non-existent member email"
        ));
        newGroup.setMembers(new HashSet<>(nonExistentMembers));
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
            Team updated = new Team();
            updated.setId(toUpdate.getId());
            updated.setName("Dev#test team 1 updated");
            updated.setReviewer(true);
            HttpEntity<Team> httpEntity = new HttpEntity<>(updated);
            URI updateUrl = URI.create(testUrl + "/" + updated.getId());
            ResponseEntity<Void> response = restTemplate
                    .exchange(updateUrl, HttpMethod.PUT, httpEntity, Void.class);
            Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            availableTeams = teamRepo.findAll();
            Assertions.assertThat(availableTeams.getLast()).isEqualTo(updated);
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
        teamRepo.deleteAll(teamRepo.findAllByNameStartingWith("Dev#test"));
        memberRepo.deleteAllByNameStartingWith("Dev#test");
        roleRepo.deleteAllByNameStartingWith("Dev#test");
    }
}

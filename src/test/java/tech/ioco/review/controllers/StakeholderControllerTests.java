package tech.ioco.review.controllers;

import org.apache.coyote.Response;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import tech.ioco.review.data.MemberRepository;
import tech.ioco.review.data.StakeholderRepository;
import tech.ioco.review.data.TeamRepository;
import tech.ioco.review.entity.Member;
import tech.ioco.review.entity.Stakeholder;
import tech.ioco.review.entity.Team;

import java.util.*;
import java.net.URI;


import org.assertj.core.api.Assertions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StakeholderControllerTests {
    TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    TeamRepository teamRepo;
    @Autowired
    MemberRepository memberRepo;
    @Autowired
    StakeholderRepository stakeholderRepo;
    private Team mockTeam;
    private List<Stakeholder> teamStakeholders;
    private List<Stakeholder> testStakeholders;

    private String testUrl;

    @BeforeEach
    public void organize() {
        testStakeholders = stakeholderRepo.findByNameStartingWith("test");
        mockTeam = teamRepo.findById(mockTeam.getId()).get();
    }

    @BeforeAll
    public void setUp() {
        mockTeam = teamRepo.save(new Team(
                null,
                "Team test",
                true
        ));
        List<Member> stakeholders = new ArrayList<Member>();
        stakeholders.add(new Member(
                        null,
                        "test 1 name",
                        "test 1 surname",
                        "test 1 email"
                )
        );
        stakeholders.add(
                new Member(
                        null,
                        "test 2 name",
                        "test 2 surname",
                        "test 2 email"
                )
        );
        testStakeholders = new ArrayList<>();
        stakeholders.forEach(member -> {
            testStakeholders.add(stakeholderRepo.save(new Stakeholder(member)));
        });
        teamStakeholders = testStakeholders;
        testUrl = "http://localhost:8080/teams/" + mockTeam.getId() + "/stakeholders";
    }

    @Test
    @Order(1)
    void getRequestTest() {
        ResponseEntity<Stakeholder[]> response = restTemplate.getForEntity(
                testUrl, Stakeholder[].class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(2)
    void getRequestWithNonExistentTeamTest() {
        UUID nonExistentId = UUID.randomUUID();
        Stakeholder stakeholderToGet = testStakeholders.getFirst();
        ResponseEntity<Stakeholder[]> response = restTemplate.getForEntity(
                "http://localhost:8080/teams/" + nonExistentId + "/stakeholders",
                Stakeholder[].class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(3)
    void postRequestTest() {
        Stakeholder entity = new Stakeholder(
                new Member(
                        UUID.randomUUID(),
                        "test 3 name",
                        "test 3 surname",
                        "test 3 email"
                )
        );
        ResponseEntity<Void> postResponse = restTemplate.postForEntity(
                testUrl, entity, Void.class
        );
        Assertions.assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ResponseEntity<Stakeholder> getResponse = restTemplate.getForEntity(
                postResponse.getHeaders().getLocation(), Stakeholder.class
        );
        testStakeholders.add(getResponse.getBody());
        Assertions.assertThat(stakeholderRepo.findAll()).contains(getResponse.getBody());
    }

    @Test
    @Order(4)
    void postStakeholderToAGroupThatDoesNotExistFailureTest() {
        UUID nonExistentTeamId = UUID.randomUUID();
        Stakeholder entity = new Stakeholder(
                new Member(
                        UUID.randomUUID(),
                        "test 4 name",
                        "test 4 surname",
                        "test 4 email"
                )
        );
        ResponseEntity<Void> response = restTemplate.postForEntity(
                "http://localhost:8080/teams/" + nonExistentTeamId + "/stakeholders",
                entity, Void.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(4)
    void getStakeHolderByIdRequestTest() {
        Stakeholder toGet = testStakeholders.getLast();
        ResponseEntity<Stakeholder> response = restTemplate.getForEntity(
                testUrl + "/" + toGet.getId(),
                Stakeholder.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo(toGet);
    }

    @Test
    @Order(5)
    void getStakeholderThatDoesNotGetExistFailureTest() {
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<Stakeholder> response = restTemplate.getForEntity(
                testUrl + "/" + nonExistentId,
                Stakeholder.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(6)
    void updateStakeholderRequest() {
        Stakeholder toUpdate = testStakeholders.getLast();
        toUpdate.setName("test 3 name updated");
        toUpdate.setSurname("test 3 surname updated");
        toUpdate.setEmail("test 3 email updated");
        //Update properties outside of member
        toUpdate.setStaffMember(true);
        toUpdate.setReviewer(true);
        toUpdate.setReviewee(true);
        HttpEntity<Stakeholder> httpEntity = new HttpEntity<Stakeholder>(toUpdate);
        ResponseEntity<Void> response = restTemplate.exchange(
                URI.create(testUrl + "/" + toUpdate.getId()),
                HttpMethod.PUT, httpEntity, Void.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Optional<Stakeholder> stakeholderOptional = stakeholderRepo.findById(toUpdate.getId());
        if (stakeholderOptional.isPresent()) {
            Stakeholder updated = stakeholderOptional.get();
            Assertions.assertThat(toUpdate.getName()).isEqualTo(updated.getName());
            Assertions.assertThat(toUpdate.getSurname()).isEqualTo(updated.getSurname());
            Assertions.assertThat(toUpdate.getEmail()).isEqualTo(updated.getEmail());
            Assertions.assertThat(toUpdate.isStaffMember()).isEqualTo(updated.isStaffMember());
            Assertions.assertThat(toUpdate.isReviewer()).isEqualTo(updated.isReviewer());
            Assertions.assertThat(toUpdate.isReviewee()).isEqualTo(updated.isReviewee());
        }

    }

    @Test
    @Order(6)
    void updateStakeholderThatDoesNotBelongToTheSpecifiedTeamFailureTest() {
        Stakeholder toUpdate = testStakeholders.getFirst();
        toUpdate.setName("test 1 name updated");
        toUpdate.setSurname("test 1 surname updated");
        toUpdate.setEmail("test 1 email updated");
        HttpEntity<Stakeholder> httpEntity = new HttpEntity<Stakeholder>(toUpdate);
        ResponseEntity<Void> response = restTemplate.exchange(
                URI.create(testUrl + "/" + toUpdate.getId()),
                HttpMethod.PUT, httpEntity, Void.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(7)
    void updateStakeholderFromATeamThatDoesNotExist() {
        UUID nonExistentTeamId = UUID.randomUUID();
        Stakeholder toUpdate = testStakeholders.getLast();
        toUpdate.setName("test 3 name updated");
        toUpdate.setSurname("test 3 surname updated");
        toUpdate.setEmail("test 3 email updated");
        HttpEntity<Stakeholder> httpEntity = new HttpEntity<Stakeholder>(toUpdate);
        ResponseEntity<Void> response = restTemplate.exchange(
                URI.create("http://localhost:8080/teams/" +
                        nonExistentTeamId +
                        "/stakeholders/" + toUpdate.getId()),
                HttpMethod.PUT, httpEntity, Void.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(8)
    void deleteStakeholderRequestTest() {
        Stakeholder toDelete = testStakeholders.getLast();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                URI.create(testUrl + "/" + toDelete.getId()),
                HttpMethod.DELETE,
                null, Void.class
        );
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<Stakeholder[]> getResponse = restTemplate.getForEntity(
                testUrl, Stakeholder[].class
        );
        Assertions.assertThat(getResponse.getBody()).doesNotContain(toDelete);
    }

    @Test
    @Order(9)
    void deleteStakeholderFromANonExistentTeamFailureTest() {
        UUID nonExistentIdStakeholderId = UUID.randomUUID();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                URI.create(testUrl + "/" + nonExistentIdStakeholderId),
                HttpMethod.DELETE,
                null, Void.class
        );
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(10)
    void deleteStakeholderNotBelongingToTheTeamInContextFailureTest() {
        Stakeholder toDelete = testStakeholders.getFirst();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                URI.create(testUrl + "/" + toDelete.getId()),
                HttpMethod.DELETE,
                null, Void.class
        );
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @AfterAll
    public void conclude() {
        Team dataBaseTeam = teamRepo.findById(mockTeam.getId()).get();
        dataBaseTeam.setStakeholders(new HashSet<>());
        teamRepo.save(dataBaseTeam);
        teamRepo.delete(dataBaseTeam);
        stakeholderRepo.deleteAll(stakeholderRepo.findByNameStartingWith("test"));
    }
}

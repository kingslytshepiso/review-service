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
import tech.ioco.review.data.*;
import tech.ioco.review.entity.*;

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
    @Autowired
    RoleRepository roleRepo;

    @Autowired
    OrganizationRepository orgRepo;
    private Team mockTeam;
    private List<Stakeholder> teamStakeholders;
    private List<Stakeholder> testStakeholders;

    private Organisation testOrganization;
    private Role testRole;
    private String testUrl;

    @BeforeEach
    public void organize() {
        testStakeholders = stakeholderRepo.findAllByNameStartingWith("Dev#test");
        mockTeam = teamRepo.findById(mockTeam.getId()).get();
    }

    @BeforeAll
    public void setUp() {
        mockTeam = teamRepo.save(new Team(
                null,
                "Dev#test team 1",
                true
        ));
        testOrganization = orgRepo.save(new Organisation(null, "Dev#test iOCO Digital"));
        testRole = roleRepo.save(new Role(null, "Dev#test 'Test Analyst'"));
        List<Member> stakeholders = new ArrayList<Member>();
        stakeholders.add(new Member(
                        null,
                        "Dev#test stakeholder 1 name",
                        "Dev#test stakeholder 1 surname",
                        "Dev#test stakeholder 1 email"
                )
        );
        stakeholders.add(
                new Member(
                        null,
                        "Dev#test stakeholder 2 name",
                        "Dev#test stakeholder 2 surname",
                        "Dev#test stakeholder 2 email"
                )
        );
        testStakeholders = new ArrayList<>();
        stakeholders.forEach(member -> {
            Stakeholder item = new Stakeholder(member);
            item.setRole(testRole);
            item.setOrganisation(testOrganization);
            testStakeholders.add(item);
        });
        Team updatedTeam = mockTeam;
        List<Stakeholder> savedStakeholders = stakeholderRepo.saveAll(testStakeholders);
        updatedTeam.setStakeholders(new HashSet<Stakeholder>(savedStakeholders));
        mockTeam = teamRepo.save(updatedTeam);
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
                        null,
                        "Dev#test stakeholder 3 name",
                        "Dev#test stakeholder 3 surname",
                        "Dev#test stakeholder 3 email"
                )
        );
        entity.setOrganisation(testOrganization);
        entity.setRole(testRole);
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
    void postStakeholderToATeamThatDoesNotExistFailureTest() {
        UUID nonExistentTeamId = UUID.randomUUID();
        Stakeholder entity = new Stakeholder(
                new Member(
                        null,
                        "Dev#test stakeholder 4 name",
                        "Dev#test stakeholder 4 surname",
                        "Dev#test stakeholder 4 email"
                )
        );
        entity.setOrganisation(testOrganization);
        entity.setRole(testRole);
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
        toUpdate.setName("Dev#test stakeholder 3 name updated");
        toUpdate.setSurname("Dev#test stakeholder 3 surname updated");
        toUpdate.setEmail("Dev#test stakeholder 3 email updated");
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
        toUpdate.setId(UUID.randomUUID());
        toUpdate.setName("Dev#test stakeholder 1 name updated");
        toUpdate.setSurname("Dev#test stakeholder 1 surname updated");
        toUpdate.setEmail("Dev#test stakeholder 1email updated");
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
        toUpdate.setName("Dev#test stakeholder 3 name updated");
        toUpdate.setSurname("Dev#test stakeholder 3 surname updated");
        toUpdate.setEmail("Dev#test stakeholder 3 email updated");
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
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                URI.create(testUrl + "/" + nonExistentId),
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
        stakeholderRepo.deleteAll(stakeholderRepo.findAllByNameStartingWith("Dev#test"));
        roleRepo.deleteAllByNameStartingWith("Dev#test");
        orgRepo.deleteAll(orgRepo.findAllByNameStartingWith("Dev#test"));
    }
}

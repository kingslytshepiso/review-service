package tech.ioco.review.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import tech.ioco.review.data.MemberRepository;
import tech.ioco.review.data.TeamRepository;
import tech.ioco.review.entity.Member;
import tech.ioco.review.entity.Team;

import java.net.URI;
import java.util.*;

import org.assertj.core.api.Assertions;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MemberControllerTests {

    TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    TeamRepository teamRepo;

    @Autowired
    MemberRepository memberRepo;

    private Team mockTeam;

    private List<Member> teamMockMember;

    private List<Member> testMembers;

    private String testUrl;

    @BeforeAll
    public void setUp() {
        mockTeam = teamRepo.save(new Team(
                null,
                "Team test",
                true
        ));
        testMembers = new ArrayList<Member>();
        testMembers.add(new Member(
                null,
                "member 1 test name",
                "member 1 test surname",
                "member 1 test email"
        ));
        testMembers.add(new Member(
                null,
                "member 2 test name",
                "member 2 test surname",
                "member 2 test email"
        ));
        teamMockMember = memberRepo.saveAll(testMembers);
        testMembers = teamMockMember;
        testUrl = "http://localhost:8080/teams/" + mockTeam.getId() + "/members";
    }

    @Test
    @Order(1)
    void getRequestTest() {
        ResponseEntity<Member[]> response = restTemplate
                .getForEntity(testUrl, Member[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(2)
    void postRequestTest() throws Exception {
        ResponseEntity<Void> postResponse = restTemplate.postForEntity(
                testUrl, teamMockMember.getFirst(), Void.class
        );
        Assertions.assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Member> getResponse = restTemplate.getForEntity(
                postResponse.getHeaders().getLocation(),
                Member.class
        );
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getResponse.getBody()).isEqualTo(teamMockMember.getFirst());

        ResponseEntity<Member[]> getAllResponse = restTemplate.getForEntity(
                testUrl, Member[].class
        );
        Assertions.assertThat(getAllResponse.getBody()).contains(getResponse.getBody());
    }

    @Test
    @Order(3)
    void getTeamMemberByIdRequestTest() {
        Member memberToGet = teamMockMember.getFirst();
        ResponseEntity<Member> response = restTemplate.getForEntity(
                testUrl + "/" + memberToGet.getId(), Member.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo(memberToGet);
    }

    @Test
    @Order(4)
    void getTeamMemberWithANonExistentTeamTest() {
        Member memberToGet = teamMockMember.getFirst();
        ResponseEntity<Member> response = restTemplate.getForEntity(
                "http://localhost:8080/teams/" + UUID.randomUUID() +
                        "/" + memberToGet.getId(),
                Member.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(5)
    void getTeamMemberThatDoesNotExistTest() {
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<Member> response = restTemplate.getForEntity(
                testUrl + "/" + nonExistentId, Member.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    @Order(6)
    void deleteTeamMemberThatDoesNotExistRequestFailureTest() {
        UUID nonExistentId = UUID.randomUUID();
        ResponseEntity<Void> response = restTemplate.exchange(
                testUrl + "/" + nonExistentId,
                HttpMethod.DELETE,
                null,
                Void.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(7)
    void deleteTeamMemberFromAGroupThatDoesNotExistRequestFailureTest() {
        Member memberToDelete = teamMockMember.getFirst();
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:8080/teams/" +
                        UUID.randomUUID() + "/" + memberToDelete.getId(),
                HttpMethod.DELETE, null, Void.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Order(8)
    void deleteTeamMemberRequestTest() {
        Member toDelete = teamMockMember.getFirst();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                URI.create(testUrl + "/" + toDelete.getId()),
                HttpMethod.DELETE,
                null, Void.class
        );
        teamMockMember = memberRepo.findByTeams(mockTeam);
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(teamMockMember).doesNotContain(toDelete);
    }

    @AfterAll
    public void conclude() {
        Team databaseTeam = teamRepo.findById(mockTeam.getId()).get();
        databaseTeam.setMembers(new HashSet<>());
        Team updatedTeam = teamRepo.save(databaseTeam);
        teamRepo.delete(mockTeam);
        memberRepo.deleteAll(testMembers);
    }
}

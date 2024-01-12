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
import tech.ioco.review.data.RoleRepository;
import tech.ioco.review.data.TeamRepository;
import tech.ioco.review.entity.Member;
import tech.ioco.review.entity.Role;
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

    @Autowired
    RoleRepository roleRepo;
    private Team mockTeam;
    private List<Member> teamMockMembers;
    private List<Member> testMembers;
    private Role testRole;
    private String testUrl;

    @BeforeAll
    public void setUp() {
        mockTeam = teamRepo.save(new Team(
                null,
                "Dev#test team 1",
                true
        ));
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
        teamMockMembers = memberRepo.saveAll(testMembers);
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
        Member toAdd = teamMockMembers.getFirst();
        toAdd.setRole(testRole);
        ResponseEntity<Void> postResponse = restTemplate.postForEntity(
                testUrl, toAdd, Void.class
        );
        Assertions.assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        ResponseEntity<Member> getResponse = restTemplate.getForEntity(
                postResponse.getHeaders().getLocation(),
                Member.class
        );
        Assertions.assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(getResponse.getBody()).isEqualTo(teamMockMembers.getFirst());

        ResponseEntity<Member[]> getAllResponse = restTemplate.getForEntity(
                testUrl, Member[].class
        );
        Assertions.assertThat(getAllResponse.getBody()).contains(getResponse.getBody());
    }

    @Test
    @Order(2)
    void postTeamMemberThatDoesNotExistTest() {
        Member toAdd = new Member(
                null,
                "Dev#test member 3 name",
                "Dev#test member 3 surname",
                "Dev#test member 3 email"
        );
        toAdd.setRole(testRole);
        ResponseEntity<Void> postResponse = restTemplate.postForEntity(
                testUrl, toAdd, Void.class
        );
        Assertions.assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ResponseEntity<Member> getResponse = restTemplate.getForEntity(
                postResponse.getHeaders().getLocation(), Member.class
        );
        ResponseEntity<Member[]> getAllResponse = restTemplate.getForEntity(
                testUrl, Member[].class
        );
        Assertions.assertThat(getAllResponse.getBody()).contains(getResponse.getBody());
    }

    @Test
    @Order(3)
    void getTeamMemberByIdRequestTest() {
        Member memberToGet = teamMockMembers.getFirst();
        ResponseEntity<Member> response = restTemplate.getForEntity(
                testUrl + "/" + memberToGet.getId(), Member.class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getBody()).isEqualTo(memberToGet);
    }

    @Test
    @Order(4)
    void getTeamMemberWithANonExistentTeamTest() {
        Member memberToGet = teamMockMembers.getFirst();
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
        Member memberToDelete = teamMockMembers.getFirst();
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
        Member toDelete = teamMockMembers.getFirst();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                URI.create(testUrl + "/" + toDelete.getId()),
                HttpMethod.DELETE,
                null, Void.class
        );
        teamMockMembers = memberRepo.findByTeams(mockTeam);
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        Assertions.assertThat(teamMockMembers).doesNotContain(toDelete);
    }

    @AfterAll
    public void conclude() {
        Team databaseTeam = teamRepo.findById(mockTeam.getId()).get();
        databaseTeam.setMembers(new HashSet<>());
        teamRepo.save(databaseTeam);
        teamRepo.delete(mockTeam);
        memberRepo.deleteAllByNameStartingWith("Dev#test");
        roleRepo.deleteAllByNameStartingWith("Dev#test");
    }
}

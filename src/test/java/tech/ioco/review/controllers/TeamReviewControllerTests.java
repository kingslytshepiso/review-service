package tech.ioco.review.controllers;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import org.assertj.core.api.Assertions;
import tech.ioco.review.repository.ReviewRepository;
import tech.ioco.review.repository.TeamRepository;
import tech.ioco.review.entity.Review;
import tech.ioco.review.entity.Status;
import tech.ioco.review.entity.Team;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TeamReviewControllerTests {
    TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    TeamRepository teamRepo;
    @Autowired
    ReviewRepository reviewRepo;
    private Team mockTeam;
    private List<Review> testReviews;
    private String testUrl;

    @BeforeAll
    public void setUp() {
        mockTeam = teamRepo.save(new Team(
                null,
                "Dev#test team 1",
                true
        ));
        List<Review> reviews = new ArrayList<>();
        reviews.add(new Review(
                null,
                "Dev#test Review 1 name",
                LocalDate.now(),
                LocalDate.now().plusDays(30L),
                Status.CREATED
        ));
        reviews.add(new Review(
                null,
                "Dev#test Review 2 name",
                LocalDate.now(),
                LocalDate.now().plusDays(30L),
                Status.CREATED
        ));
        List<Review> savedReviews = reviewRepo.saveAll(new HashSet<Review>(reviews));
        mockTeam.setReviews(new HashSet<>(savedReviews));
        mockTeam = teamRepo.save(mockTeam);
        testUrl = "http://localhost:8080/teams/" + mockTeam.getId() + "/reviews";

    }

    @BeforeEach
    public void organize() {
        testReviews = reviewRepo.findAllByNameStartingWith("Dev#test");
        mockTeam = teamRepo.findById(mockTeam.getId()).get();
    }

    @Test
    void getRequestTest() {
        ResponseEntity<Review[]> response = restTemplate.getForEntity(
                testUrl, Review[].class
        );
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }


    @AfterAll
    public void conclude() {
        Team dataBaseTeam = teamRepo.findById(mockTeam.getId()).get();
        dataBaseTeam.setReviews(new HashSet<>());
        teamRepo.save(dataBaseTeam);
        teamRepo.deleteAllByNameStartingWith("Dev#test");
        reviewRepo.deleteAllByNameStartingWith("Dev#test");
    }
}

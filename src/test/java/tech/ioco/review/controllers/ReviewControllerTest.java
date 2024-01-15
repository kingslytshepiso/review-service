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
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;
import tech.ioco.review.entity.Review;

import org.assertj.core.api.Assertions;
import tech.ioco.review.entity.Status;
import tech.ioco.review.entity.Team;
import tech.ioco.review.repository.ReviewRepository;
import tech.ioco.review.repository.TeamRepository;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewControllerTest {
    TestRestTemplate restTemplate = new TestRestTemplate();
    @Autowired
    private ReviewRepository reviewRepo;
    @Autowired
    private TeamRepository teamRepo;
    private final String testUrl = "http://localhost:8080/reviews";
    List<Review> testReviews = new ArrayList<>();
    List<Team> testTeams = new ArrayList<>();

    @Test
    @Order(1)
    void getRequestTest() {
        ResponseEntity<Review[]> response = restTemplate.getForEntity(testUrl, Review[].class);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @Order(2)
    void postAReviewRequestWithoutTeamsTest() {
        Review toAdd = new Review(
                null,
                "Dev#test Review 1 name",
                LocalDate.now(),
                LocalDate.now().plusDays(30L),
                Status.CREATED
        );
        ResponseEntity<Void> postResponse = restTemplate.postForEntity(
                testUrl, toAdd, Void.class
        );
        Assertions.assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ResponseEntity<Review> getSavedReviewResponse = restTemplate.getForEntity(
                postResponse.getHeaders().getLocation(), Review.class
        );
        ResponseEntity<Review[]> getAllResponse = restTemplate.getForEntity(
                testUrl, Review[].class
        );
        Assertions.assertThat(getAllResponse.getBody()).contains(getSavedReviewResponse.getBody());
    }

    @Test
    @Order(3)
    @Disabled
    void postAReviewRequestWithTeamsTest() {
        Review toAdd = new Review(
                null,
                "Dev#test Review 2 name",
                LocalDate.now(),
                LocalDate.now().plusDays(30L),
                Status.CREATED
        );
        Set<Team> reviewTeams = new HashSet<>(teamRepo.findAll());
        toAdd.setTeams(reviewTeams);
        ResponseEntity<Void> postResponse = restTemplate.postForEntity(
                testUrl, toAdd, Void.class
        );
        Assertions.assertThat(postResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ResponseEntity<Review> getSavedReviewResponse = restTemplate.getForEntity(
                postResponse.getHeaders().getLocation(), Review.class
        );
        ResponseEntity<Review[]> getAllResponse = restTemplate.getForEntity(
                testUrl, Review[].class
        );
        Assertions.assertThat(getSavedReviewResponse.getBody().getTeams()).isEqualTo(new HashSet<>(testTeams));
        Assertions.assertThat(getAllResponse.getBody()).contains(getSavedReviewResponse.getBody());
    }

    @Test
    @Order(4)
    void updateReviewTest() {
        Review toUpdate = testReviews.getLast();
        toUpdate.setName("Dev#test Review 2 updated");
        Set<Team> reviewTeams = teamRepo.findAllByReviews(toUpdate);
        toUpdate.setTeams(reviewTeams);
        HttpEntity<Review> entity = new HttpEntity<>(toUpdate);
        ResponseEntity<Void> updateResponse = restTemplate.exchange(
                URI.create(testUrl + "/" + toUpdate.getId()),
                HttpMethod.PUT, entity, Void.class
        );
        Assertions.assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<Review> getResponse = restTemplate.getForEntity(
                testUrl + "/" + toUpdate.getId(), Review.class
        );
        Assertions.assertThat(getResponse.getBody()).isEqualTo(toUpdate);
    }

    @Test
    @Order(6)
    void deleteReviewTest() {
        Review toDelete = testReviews.getLast();
        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                URI.create(testUrl + "/" + toDelete.getId()),
                HttpMethod.DELETE, null, Void.class
        );
        Assertions.assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        ResponseEntity<Review[]> getAllResponse = restTemplate.getForEntity(
                testUrl, Review[].class
        );
        Assertions.assertThat(getAllResponse.getBody()).doesNotContain(toDelete);
    }


    @BeforeAll
    public void setUp() {
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
        List<Team> teams = new ArrayList<>();
        teams.add(
                new Team(
                        null,
                        "Dev#test team 1",
                        true
                )
        );
        teams.add(
                new Team(
                        null,
                        "Dev#test team 2",
                        true
                )
        );
        testTeams = teamRepo.saveAll(teams);
        reviews.forEach(r -> {
            r.setTeams(new HashSet<>(testTeams));
        });
        testReviews = reviewRepo.saveAll(reviews);
    }

    @BeforeEach
    public void setEachUp() {
        testReviews = reviewRepo.findAllByNameStartingWith("Dev#test");
        testTeams = teamRepo.findAllByNameStartingWith("Dev#test");
    }

    @AfterAll
    public void conclude() {
        teamRepo.deleteAllByNameStartingWith("Dev#test");
        reviewRepo.deleteAllByNameStartingWith("Dev#test");
    }
}

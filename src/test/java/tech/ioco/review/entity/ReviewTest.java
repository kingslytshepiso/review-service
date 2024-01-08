package tech.ioco.review.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class ReviewTest {

    private Review review;
    private UUID id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;
    private Set<Team> groups;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        name = "Annual Review";
        startDate = LocalDate.of(2023, 1, 1);
        endDate = LocalDate.of(2023, 12, 31);
        status = Status.CREATED;
        groups = new HashSet<>();
        review = new Review(id, name, startDate, endDate, status);
        review.setTeams(groups);
    }

    @Test
    void testGetIdWhenIdIsSetThenReturnId() {
        Assertions.assertThat(review.getId()).isEqualTo(id);
    }

    @Test
    void testGetNameWhenNameIsSetThenReturnName() {
        Assertions.assertThat(review.getName()).isEqualTo(name);
    }

    @Test
    void testGetStartDateWhenStartDateIsSetThenReturnStartDate() {
        Assertions.assertThat(review.getStartDate()).isEqualTo(startDate);
    }

    @Test
    void testGetEndDateWhenEndDateIsSetThenReturnEndDate() {
        Assertions.assertThat(review.getEndDate()).isEqualTo(endDate);
    }

    @Test
    void testGetStatusWhenStatusIsSetThenReturnStatus() {
        Assertions.assertThat(review.getStatus()).isEqualTo(status);
    }

    @Test
    void testGetGroupsWhenGroupsAreSetThenReturnGroups() {
        Assertions.assertThat(review.getTeams()).isEqualTo(groups);
    }

    @Test
    void testEqualsWhenTwoEntitiesHaveSameIdThenReturnTrue() {
        Review anotherReview = new Review(id);
        Assertions.assertThat(review).isEqualTo(anotherReview);
    }

    @Test
    void testEqualsWhenTwoEntitiesHaveDifferentIdsThenReturnFalse() {
        Review anotherReview = new Review(UUID.randomUUID());
        Assertions.assertThat(review).isNotEqualTo(anotherReview);
    }

    @Test
    void testHashCodeWhenTwoEntitiesHaveSameIdThenReturnEqualHashCodes() {
        Review anotherReview = new Review(id);
        Assertions.assertThat(review.hashCode()).isEqualTo(anotherReview.hashCode());
    }

    @Test
    void testToStringThenReturnStringWithId() {
        String expectedString = "tech.ioco.review.Review[ id=" + id + " ]";
        Assertions.assertThat(review.toString()).isEqualTo(expectedString);
    }
}

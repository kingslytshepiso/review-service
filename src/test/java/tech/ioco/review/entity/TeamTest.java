package tech.ioco.review.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class TeamTest {

    private Team group;
    private UUID id;
    private String name;
    private boolean isReviewer;
    private Set<Review> reviews;
    private Set<Member> members;
    private Set<Stakeholder> stakeholders;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        name = "Test Team";
        isReviewer = true;
        reviews = new HashSet<>();
        members = new HashSet<>();
        stakeholders = new HashSet<>();
        group = new Team(id, name, isReviewer);
    }

    @Test
    void testGetIdAndSetIdWhenIdIsSetThenIdIsRetrieved() {
        // Arrange
        UUID newId = UUID.randomUUID();
        group.setId(newId);

        // Act
        UUID retrievedId = group.getId();

        // Assert
        Assertions.assertThat(retrievedId).isEqualTo(newId);
    }

    @Test
    void testGetNameAndSetNameWhenNameIsSetThenNameIsRetrieved() {
        // Arrange
        String newName = "New Test Team";
        group.setName(newName);

        // Act
        String retrievedName = group.getName();

        // Assert
        Assertions.assertThat(retrievedName).isEqualTo(newName);
    }

    @Test
    void testIsReviewerAndSetReviewerWhenReviewerFlagIsSetThenFlagIsRetrieved() {
        // Arrange
        group.setReviewer(false);

        // Act
        boolean retrievedReviewerFlag = group.isReviewer();

        // Assert
        Assertions.assertThat(retrievedReviewerFlag).isFalse();
    }

    @Test
    void testGetReviewsAndSetReviewsWhenReviewsAreSetThenReviewsAreRetrieved() {
        // Arrange
        Review review = new Review();
        reviews.add(review);
        group.setReviews(reviews);

        // Act
        Set<Review> retrievedReviews = group.getReviews();

        // Assert
        Assertions.assertThat(retrievedReviews).containsExactly(review);
    }

    @Test
    void testGetMembersAndSetMembersWhenMembersAreSetThenMembersAreRetrieved() {
        // Arrange
        Member member = new Member();
        members.add(member);
        group.setMembers(members);

        // Act
        Set<Member> retrievedMembers = group.getMembers();

        // Assert
        Assertions.assertThat(retrievedMembers).containsExactly(member);
    }

    @Test
    void testGetStakeholdersAndSetStakeholdersWhenStakeholdersAreSetThenStakeholdersAreRetrieved() {
        // Arrange
        Stakeholder stakeholder = new Stakeholder();
        stakeholders.add(stakeholder);
        group.setStakeholders(stakeholders);

        // Act
        Set<Stakeholder> retrievedStakeholders = group.getStakeholders();

        // Assert
        Assertions.assertThat(retrievedStakeholders).containsExactly(stakeholder);
    }

    @Test
    void testHashCodeWhenTwoEntitiesWithSameIdThenHashCodesAreEqual() {
        // Arrange
        Team anotherGroup = new Team(id, "Another Test Team", false);

        // Act
        int groupHashCode = group.hashCode();
        int anotherGroupHashCode = anotherGroup.hashCode();

        // Assert
        Assertions.assertThat(groupHashCode).isEqualTo(anotherGroupHashCode);
    }

    @Test
    void testEqualsWhenTwoEntitiesWithSameIdThenEqualsReturnsTrue() {
        // Arrange
        Team anotherGroup = new Team(id, "Another Test Team", false);

        // Act & Assert
        Assertions.assertThat(group).isEqualTo(anotherGroup);
    }

    @Test
    void testToStringWhenCalledThenStringContainsId() {
        // Act
        String groupString = group.toString();

        // Assert
        Assertions.assertThat(groupString).contains(id.toString());
    }
}

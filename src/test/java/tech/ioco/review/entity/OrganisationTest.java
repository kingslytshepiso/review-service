package tech.ioco.review.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class OrganisationTest {

    private Organisation organisation;
    private UUID id;
    private String name;
    private Set<Stakeholder> stakeholders;

    @BeforeEach
    void setUp() {
        organisation = new Organisation();
        id = UUID.randomUUID();
        name = "IocoTech";
        stakeholders = new HashSet<>();
        stakeholders.add(new Stakeholder());
    }

    @Test
    void testGetIdWhenIdIsSetThenReturnId() {
        // Arrange
        organisation.setId(id);
        // Act
        UUID actualId = organisation.getId();
        // Assert
        Assertions.assertThat(actualId).isEqualTo(id);
    }

    @Test
    void testGetNameWhenNameIsSetThenReturnName() {
        // Arrange
        organisation.setName(name);
        // Act
        String actualName = organisation.getName();
        // Assert
        Assertions.assertThat(actualName).isEqualTo(name);
    }

    @Test
    void testGetStakeholdersWhenStakeholdersAreSetThenReturnStakeholders() {
        // Arrange
        organisation.setStakeholders(stakeholders);
        // Act
        Set<Stakeholder> actualStakeholders = organisation.getStakeholders();
        // Assert
        Assertions.assertThat(actualStakeholders).isEqualTo(stakeholders);
    }

    @Test
    void testHashCodeWhenIdIsSetThenReturnHashCode() {
        // Arrange
        organisation.setId(id);
        int expectedHashCode = id.hashCode();
        // Act
        int actualHashCode = organisation.hashCode();
        // Assert
        Assertions.assertThat(actualHashCode).isEqualTo(expectedHashCode);
    }

    @Test
    void testEqualsWhenIdIsTheSameThenReturnTrue() {
        // Arrange
        Organisation anotherOrganisation = new Organisation(id);
        organisation.setId(id);
        // Act
        boolean isEqual = organisation.equals(anotherOrganisation);
        // Assert
        Assertions.assertThat(isEqual).isTrue();
    }

    @Test
    void testToStringWhenIdIsSetThenReturnString() {
        // Arrange
        organisation.setId(id);
        String expectedString = "tech.ioco.review.Organisation[ id=" + id + " ]";
        // Act
        String actualString = organisation.toString();
        // Assert
        Assertions.assertThat(actualString).isEqualTo(expectedString);
    }
}

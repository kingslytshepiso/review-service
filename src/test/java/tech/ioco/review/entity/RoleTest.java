package tech.ioco.review.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

//@ExtendWith(SpringExtension.class)
class RoleTest {

    @Test
    void testGetAndSetIdWhenIdIsSetThenIdIsRetrieved() {
        // Arrange
        Role role = new Role();
        UUID expectedId = UUID.randomUUID();
        role.setId(expectedId);

        // Act
        UUID actualId = role.getId();

        // Assert
        assertThat(actualId).isEqualTo(expectedId);
    }

    @Test
    void testGetAndSetNameWhenNameIsSetThenNameIsRetrieved() {
        // Arrange
        Role role = new Role();
        String expectedName = "Administrator";
        role.setName(expectedName);

        // Act
        String actualName = role.getName();

        // Assert
        assertThat(actualName).isEqualTo(expectedName);
    }

    @Test
    void testGetAndSetMembersWhenMembersAreSetThenMembersAreRetrieved() {
        // Arrange
        Role role = new Role();
        Set<Member> expectedMembers = new HashSet<>();
        expectedMembers.add(new Member()); // Assuming Member is a valid entity
        role.setMembers(expectedMembers);

        // Act
        Set<Member> actualMembers = role.getMembers();

        // Assert
        assertThat(actualMembers).isEqualTo(expectedMembers);
    }

    @Test
    void testEqualsWhenTwoObjectsHaveSameIdAndNameThenReturnTrue() {
        // Arrange
        UUID id = UUID.randomUUID();
        String name = "Administrator";
        Role role1 = new Role(id, name);
        Role role2 = new Role(id, name);

        // Act & Assert
        assertThat(role1).isEqualTo(role2);
    }

    @Test
    void testEqualsWhenTwoObjectsHaveDifferentIdsAndSameNameThenReturnFalse() {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        String name = "Administrator";
        Role role1 = new Role(id1, name);
        Role role2 = new Role(id2, name);

        // Act & Assert
        assertThat(role1).isNotEqualTo(role2);
    }

//    @Test
//    void testEqualsWhenTwoObjectsHaveSameIdAndDifferentNamesThenReturnFalse() {
//        // Arrange
//        UUID id = UUID.randomUUID();
//        Role role1 = new Role(id, "Administrator");
//        Role role2 = new Role(id, "User");
//
//        // Act & Assert
//        assertThat(role1).isNotEqualTo(role2);
//    }

    @Test
    void testHashCodeThenReturnIdHashCode() {
        // Arrange
        UUID id = UUID.randomUUID();
        Role role = new Role(id);
        int expectedHashCode = id.hashCode();

        // Act
        int actualHashCode = role.hashCode();

        // Assert
        assertThat(actualHashCode).isEqualTo(expectedHashCode);
    }

    @Test
    void testToStringThenReturnStringRepresentation() {
        // Arrange
        UUID id = UUID.randomUUID();
        Role role = new Role(id);
        String expectedString = "tech.ioco.review.Role[ id=" + id + " ]";

        // Act
        String actualString = role.toString();

        // Assert
        assertThat(actualString).isEqualTo(expectedString);
    }
}

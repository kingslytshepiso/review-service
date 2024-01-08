package tech.ioco.review.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class MemberTest {

    private Member member;
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private Role role;
    private Set<Team> groups;

    @BeforeEach
    void setUp() {
        id = UUID.randomUUID();
        name = "John";
        surname = "Doe";
        email = "john.doe@example.com";
        role = new Role(UUID.randomUUID(), "Developer");
        groups = new HashSet<>();
        member = new Member();
    }

    @Test
    void testSetAndGetId() {
        member.setId(id);
        Assertions.assertThat(member.getId()).isEqualTo(id);
    }

    @Test
    void testSetAndGetName() {
        member.setName(name);
        Assertions.assertThat(member.getName()).isEqualTo(name);
    }

    @Test
    void testSetAndGetSurname() {
        member.setSurname(surname);
        Assertions.assertThat(member.getSurname()).isEqualTo(surname);
    }

    @Test
    void testSetAndGetEmail() {
        member.setEmail(email);
        Assertions.assertThat(member.getEmail()).isEqualTo(email);
    }

    @Test
    void testSetAndGetGroups() {
        member.setTeams(groups);
        Assertions.assertThat(member.getTeams()).isEqualTo(groups);
    }

    @Test
    void testSetAndGetRole() {
        member.setRole(role);
        Assertions.assertThat(member.getRole()).isEqualTo(role);
    }

    @Test
    void testHashCode() {
        Member anotherMember = new Member();
        anotherMember.setId(id);
        member.setId(id);
        Assertions.assertThat(member.hashCode()).hasSameHashCodeAs(anotherMember.hashCode());
    }

    @Test
    void testEquals() {
        Member anotherMember = new Member();
        anotherMember.setId(id);
        member.setId(id);
        Assertions.assertThat(member).isEqualTo(anotherMember);
    }

    @Test
    void testToString() {
        member.setId(id);
        String expectedString = "tech.ioco.review.Member[ id=" + id + " ]";
        Assertions.assertThat(member.toString()).isEqualTo(expectedString);
    }
}

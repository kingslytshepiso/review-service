package tech.ioco.review.entity;

import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

//@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@Disabled
@AutoConfigureTestDatabase(replace = Replace.NONE)
class MemberDataJpaTest {

    @Container
    static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void whenSaveMember_thenFindById() {
        // Given
        Member member = new Member();
        member.setName("John");
        member.setSurname("Doe");
        member.setEmail("john.doe@example.com");

        // When
        member = entityManager.persist(member);
        Member foundMember = entityManager.find(Member.class, member.getId());

        // Then
        assertThat(foundMember).isNotNull();
        assertThat(foundMember.getName()).isEqualTo(member.getName());
        assertThat(foundMember.getSurname()).isEqualTo(member.getSurname());
        assertThat(foundMember.getEmail()).isEqualTo(member.getEmail());
    }

}
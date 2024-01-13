package tech.ioco.review.data;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import tech.ioco.review.entity.Role;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    @Transactional
    void deleteAllByNameStartingWith(String prefix);

    Optional<Role> findByName(String name);
}

package tech.ioco.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tech.ioco.review.entity.Organisation;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organisation, UUID> {
    Optional<Organisation> findByName(String name);

    List<Organisation> findAllByNameStartingWith(String name);

}

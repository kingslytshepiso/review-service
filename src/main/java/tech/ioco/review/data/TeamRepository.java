package tech.ioco.review.data;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.ioco.review.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    Boolean existsByName(String name);

}

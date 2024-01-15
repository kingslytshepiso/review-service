package tech.ioco.review.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import tech.ioco.review.entity.Review;
import tech.ioco.review.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

    Boolean existsByName(String name);

    List<Team> findAllByNameStartingWith(String name);

    @Transactional
    void deleteAllByNameStartingWith(String prefix);

    Set<Team> findAllByReviews(Review review);

}

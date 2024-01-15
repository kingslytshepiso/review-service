package tech.ioco.review.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import tech.ioco.review.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findAllByNameStartingWith(String prefix);

    @Transactional
    void deleteAllByNameStartingWith(String prefix);
}

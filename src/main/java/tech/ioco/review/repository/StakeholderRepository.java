package tech.ioco.review.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import tech.ioco.review.entity.Stakeholder;

public interface StakeholderRepository extends JpaRepository<Stakeholder, UUID> {
    List<Stakeholder> findAllByNameStartingWith(String value);
}

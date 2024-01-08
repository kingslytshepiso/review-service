package tech.ioco.review.data;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import tech.ioco.review.entity.Stakeholder;

public interface StakeholderRepository extends JpaRepository<Stakeholder, UUID> {

}

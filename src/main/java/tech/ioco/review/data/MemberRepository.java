package tech.ioco.review.data;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import tech.ioco.review.entity.Member;
import tech.ioco.review.entity.Team;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    List<Member> findByTeams(Team team);

    Optional<Member> findByEmail(String email);

    @Transactional
    void deleteAllByNameStartingWith(String prefix);
}

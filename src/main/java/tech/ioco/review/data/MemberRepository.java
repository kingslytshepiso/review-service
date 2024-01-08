package tech.ioco.review.data;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.ioco.review.entity.Member;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {

}

package tech.ioco.review.data;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tech.ioco.review.entity.Team;

@Repository
public interface TeamRepository extends JpaRepository<Team, UUID> {

//    @Query(value = "Select g.id, g.name, g.reviewer From public.group g", nativeQuery = true)
//    List<Team> findAll();
//
    Boolean existsByName(String name);

}

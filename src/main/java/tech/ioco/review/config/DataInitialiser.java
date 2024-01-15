package tech.ioco.review.config;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tech.ioco.review.repository.TeamRepository;
import tech.ioco.review.entity.Team;

@Configuration
@Profile("dev")
public class DataInitialiser {
    private final TeamRepository groupRepository;

    public DataInitialiser(TeamRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @PostConstruct
    private void init() {
        groupRepository.save(new Team(null, "Team 3", true));
    }
}

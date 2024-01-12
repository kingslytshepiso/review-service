package tech.ioco.review.controllers;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import tech.ioco.review.data.TeamRepository;
import tech.ioco.review.data.StakeholderRepository;
import tech.ioco.review.entity.Team;
import tech.ioco.review.entity.Stakeholder;

@RestController
@RequestMapping("/teams/{teamId}/stakeholders")
public class StakeholderController {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private StakeholderRepository stakeholderRepo;

    @GetMapping
    public ResponseEntity<Set<Stakeholder>> getAllStakeholders(
            @PathVariable("teamId") UUID id) {
        Optional<Team> team = teamRepository.findById(id);
        if (team.isPresent()) {
            return ResponseEntity.ok(team.get().getStakeholders());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> createStakeholder(
            @PathVariable("teamId") UUID teamId,
            @RequestBody Stakeholder stakeholder,
            UriComponentsBuilder ucb) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            Stakeholder savedStakeholder = stakeholderRepo.save(stakeholder);
            team.getStakeholders().add(savedStakeholder);
            teamRepository.save(team);
            URI groupLocation = ucb.path("teams/{teamId}/stakeholders/{stakeholderId}")
                    .buildAndExpand(team.getId(),
                            savedStakeholder.getId()).toUri();
            return ResponseEntity.created(groupLocation).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{stakeholderId}")
    public ResponseEntity<Stakeholder> getStakeholder(
            @PathVariable("teamId") UUID teamId,
            @PathVariable("stakeholderId") UUID stakeholderId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            Optional<Stakeholder> optionalStakeholder = stakeholderRepo.findById(stakeholderId);
            Boolean teamContainsStakeholder = optionalStakeholder.isPresent() ?
                    teamOptional.get().getStakeholders().contains(optionalStakeholder.get())
                    : false;
            if (teamContainsStakeholder) {
                return ResponseEntity.ok(optionalStakeholder.get());
            }
        }
        return ResponseEntity.notFound().build();
    }

    // This request assumes the processing has a connection
    // to upper group. Thus updating the stakeholder means
    // you update the stakeholder record for the group
    @PutMapping("/{stakeholderId}")
    public ResponseEntity<Void> updateStakeholder(
            @PathVariable("stakeholderId") UUID stakeholderId,
            @PathVariable("teamId") UUID teamId,
            @RequestBody Stakeholder model) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            Optional<Stakeholder> optionalStakeholder = stakeholderRepo.findById(stakeholderId);
            Boolean teamContainsStakeholder = optionalStakeholder.isPresent() ?
                    team.getStakeholders().contains(optionalStakeholder.get())
                    : false;
            if (teamContainsStakeholder) {
                stakeholderRepo.save(model);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{stakeholderId}")
    public ResponseEntity<Void> deleteStakeholder(
            @PathVariable("stakeholderId") UUID stakeholderId,
            @PathVariable("teamId") UUID teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            Optional<Stakeholder> optionalStakeholder = stakeholderRepo.findById(stakeholderId);
            Boolean teamContainsStakeholder = optionalStakeholder.isPresent() ?
                    team.getStakeholders().contains(optionalStakeholder.get())
                    : false;
            if (teamContainsStakeholder) {
                team.getStakeholders().remove(optionalStakeholder.get());
                teamRepository.save(team);
                stakeholderRepo.delete(optionalStakeholder.get());
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

}

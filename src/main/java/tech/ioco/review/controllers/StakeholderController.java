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
@RequestMapping("/groups/{groupId}/stakeholders")
public class StakeholderController {
    @Autowired
    private TeamRepository groupRepo;

    @Autowired
    private StakeholderRepository stakeholderRepo;

    @GetMapping
    public ResponseEntity<Set<Stakeholder>> getAllStakeholders(@PathVariable("groupId") UUID id) {
        Optional<Team> group = groupRepo.findById(id);
        if (group.isPresent()) {
            return ResponseEntity.ok(group.get().getStakeholders());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> createStakeholder(@RequestParam UUID stakeholderId,
            @PathVariable("groupId") UUID groupId,
            UriComponentsBuilder ucb) {
        Optional<Team> group = groupRepo.findById(stakeholderId);
        Boolean stakeholderExists = stakeholderRepo.existsById(stakeholderId);
        if (group.isPresent() && stakeholderExists) {
            Optional<Stakeholder> stakeholder = stakeholderRepo.findById(stakeholderId);
            group.get().addStakeholder(stakeholder.get());
            URI groupLocation = ucb.path("groups/{groupId}/stakeholders")
                    .buildAndExpand(group.get().getId()).toUri();
            return ResponseEntity.created(groupLocation).build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{stakeholderId}")
    public ResponseEntity<Stakeholder> getStakeholder(@PathVariable("skakeholderId") UUID id) {
        Optional<Stakeholder> stakeholder = stakeholderRepo.findById(id);
        if (stakeholder.isPresent()) {
            return ResponseEntity.ok(stakeholder.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // This request assumes the processing has a connection
    // to upper group. Thus updating the stakeholder means
    // you update the stakeholder record for the group
    @PutMapping("/{stakeholderId}")
    public ResponseEntity<Void> updateStakeholder(
            @PathVariable("stakeholderId") UUID stakeholderId,
            @PathVariable("groupId") UUID groupId,
            @RequestBody Stakeholder model) {
        Optional<Team> group = groupRepo.findById(groupId);
        Optional<Stakeholder> stakeholder = group.get().getStakeholders().stream().filter(
                item -> item.getId() == stakeholderId).findFirst();
        if (group.isPresent() && stakeholder.isPresent()) {
            model.setId(stakeholderId);
            group.get().updateStakeholder(model);
            groupRepo.save(group.get());
            // return ResponseEntity.noContent().build();
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{stakeholderId}")
    public ResponseEntity<Void> deleteStakeholder(
            @PathVariable("stakeholderId") UUID stakeholderId,
            @PathVariable("groupId") UUID groupId) {
        Optional<Team> group = groupRepo.findById(groupId);
        if (group.isPresent()) {
            group.get().removeStakeholder(stakeholderId);
        }
        return ResponseEntity.noContent().build();
    }

}

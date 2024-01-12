package tech.ioco.review.controllers;

import java.net.URI;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import tech.ioco.review.data.TeamRepository;
import tech.ioco.review.entity.Team;

// The request will identify the user with the right
// privileges to process the request
@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamRepository repo;

    @GetMapping
    public ResponseEntity<List<Team>> getAllGroups() {
        return ResponseEntity.ok(repo.findAll());
    }

    @PostMapping
    public ResponseEntity<Void> createGroup(@RequestBody Team model,
                                            UriComponentsBuilder ucb) {
        Boolean teamExists = repo.existsByName(model.getName());
        if (!teamExists) {
            Team newGroup = repo.save(model);
            URI groupLocation = ucb.path("teams/{teamId}")
                    .buildAndExpand(newGroup.getId()).toUri();
            return ResponseEntity.created(groupLocation).build();
        } else {
            // The below code assumes that the frontend client will use and
            // provide the necessary information to let the user know of an
            // existing team
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<Team> getTeam(@PathVariable("teamId") UUID id) {
        Optional<Team> team = repo.findById(id);
        if (team.isPresent()) {
            return ResponseEntity.ok(team.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<Void> updateGroup(@PathVariable("teamId") UUID id,
                                            @RequestBody Team model) {
        Boolean teamExists = repo.existsById(id);
        if (teamExists) {
            repo.save(model);
            // return ResponseEntity.noContent().build();
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable("teamId") UUID id) {
        repo.deleteById(id);
        // return ResponseEntity.noContent().build();
        return ResponseEntity.noContent().build();
    }

}

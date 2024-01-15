package tech.ioco.review.controllers;

import java.net.URI;
import java.util.*;

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

import tech.ioco.review.repository.*;
import tech.ioco.review.entity.*;

// The request will identify the user with the right
// privileges to process the request
@RestController
@RequestMapping("/teams")
public class TeamController {

    @Autowired
    private TeamRepository teamRepo;
    @Autowired
    private MemberRepository memberRepo;
    @Autowired
    private StakeholderRepository stakeholderRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private OrganizationRepository orgRepo;

    @Autowired
    private ReviewRepository reviewRepo;


    @GetMapping
    public ResponseEntity<List<Team>> getAllGroups() {
        return ResponseEntity.ok(teamRepo.findAll());
    }

    public Stakeholder createStakeholder(Stakeholder s) {
        Optional<Role> roleOptional = s.getRole() != null ?
                roleRepo.findByName(s.getRole().getName())
                : Optional.empty();
        if (roleOptional.isEmpty() && s.getRole() != null)//if the role instance does not exist in the database
            s.setRole(roleRepo.save(s.getRole()));
        else
            s.setRole(roleOptional.get());
        Optional<Organisation> orgOptional = s.getOrganisation() != null ?
                orgRepo.findByName(s.getOrganisation().getName())
                : Optional.empty();
        if (orgOptional.isEmpty() && s.getRole() != null)//if the org instance does not exist in the database
            s.setOrganisation(orgRepo.save(s.getOrganisation()));
        else
            s.setOrganisation(orgOptional.get());
        return stakeholderRepo.save(s);
    }

    public Member createMember(Member m) {
        Optional<Role> roleOptional = m.getRole() != null ?
                roleRepo.findByName(m.getRole().getName())
                : Optional.empty();
        if (roleOptional.isEmpty() && m.getRole() != null) {//if the role instance does not exist in the database
            Role savedRole = roleRepo.save(m.getRole());
            m.setRole(savedRole);
        }
        return memberRepo.save(m);
    }

    public Team resolveAndSaveTeam(Team model) {
        Set<Review> teamReviews = new HashSet<>();
        model.getReviews().forEach(r -> {
            if (r.getId() != null) {
                Optional<Review> review = reviewRepo.findById(r.getId());
                if (review.isEmpty())
                    teamReviews.add(reviewRepo.save(r));
                else
                    teamReviews.add(review.get());
            } else
                teamReviews.add(reviewRepo.save(r));
        });
        model.setReviews(teamReviews);
        Set<Member> teamMembers = new HashSet<>();
        model.getMembers().forEach(m -> {
            if (m.getId() != null) {
                Optional<Member> member = memberRepo.findById(m.getId());
                if (member.isEmpty())
                    teamMembers.add(createMember(m));
                else
                    teamMembers.add(member.get());
            } else
                teamMembers.add(createMember(m));
        });
        model.setMembers(teamMembers);
        Set<Stakeholder> teamStakeholders = new HashSet<>();
        model.getStakeholders().forEach(s -> {
            if (s.getId() != null) {
                Optional<Stakeholder> stakeholder = stakeholderRepo.findById(s.getId());
                if (stakeholder.isEmpty())
                    teamStakeholders.add(createStakeholder(s));
                else
                    teamStakeholders.add(stakeholder.get());
            } else
                teamStakeholders.add(createStakeholder(s));
        });
        model.setStakeholders(teamStakeholders);
        return teamRepo.save(model);
    }

    @PostMapping
    public ResponseEntity<Void> createTeam(@RequestBody Team model,
                                           UriComponentsBuilder ucb) {
        Boolean teamExists = teamRepo.existsByName(model.getName());
        if (!teamExists) {
            Team savedTeam = resolveAndSaveTeam(model);
            URI teamLocation = ucb.path("teams/{teamId}")
                    .buildAndExpand(savedTeam.getId()).toUri();
            return ResponseEntity.created(teamLocation).build();
        } else {
            // The below code assumes that the frontend client will use and
            // provide the necessary information to let the user know of an
            // existing team
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<Team> getTeam(@PathVariable("teamId") UUID id) {
        Optional<Team> team = teamRepo.findById(id);
        if (team.isPresent()) {
            return ResponseEntity.ok(team.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<Void> updateGroup(
            @PathVariable("teamId") UUID id,
            @RequestBody Team model) {
        Optional<Team> teamOptional = teamRepo.findById(id);
        if (teamOptional.isPresent()) {
            resolveAndSaveTeam(model);
            // return ResponseEntity.noContent().build();
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<Void> deleteTeam(@PathVariable("teamId") UUID id) {
        teamRepo.deleteById(id);
        // return ResponseEntity.noContent().build();
        return ResponseEntity.noContent().build();
    }

}

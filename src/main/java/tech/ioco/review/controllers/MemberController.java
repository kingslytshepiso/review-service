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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import tech.ioco.review.data.TeamRepository;
import tech.ioco.review.data.MemberRepository;
import tech.ioco.review.entity.Team;
import tech.ioco.review.entity.Member;

@RestController
@RequestMapping("/teams/{teamId}/members")
public class MemberController {
    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private MemberRepository memberRepo;

    @GetMapping
    public ResponseEntity<Set<Member>> geAllMembers(
            @PathVariable("teamId") UUID groupId) {
        Optional<Team> team = teamRepo.findById(groupId);
        if (team.isPresent()) {
            return ResponseEntity.ok(team.get().getMembers());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> createMember(@PathVariable("teamId") UUID teamId,
                                             @RequestBody Member model,
                                             UriComponentsBuilder ucb) {
        Optional<Team> team = teamRepo.findById(teamId);
        Boolean memberExists = memberRepo.existsById(model.getId());
        if (team.isPresent() && memberExists) {
            Optional<Member> member = memberRepo.findById(model.getId());
            team.get().addMember(member.get());
            teamRepo.save(team.get());
            URI groupLocation = ucb.path("teams/{teamsId}/members/{memberId}")
                    .buildAndExpand(team.get().getId(), member.get().getId()).toUri();
            return ResponseEntity.created(groupLocation).build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /*
    Assumption: The search for a member is specific to the group
    specified by the request
    * */
    @GetMapping("/{memberId}")
    public ResponseEntity<Member> getMember(
            @PathVariable("teamId") UUID teamId,
            @PathVariable("memberId") UUID memberId) {
        /*
        Both team and member should exist, otherwise return 404;
        */
        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isPresent()) {
            Optional<Member> optionalMember = memberRepo.findById(memberId);
            Boolean teamContainsMember = optionalMember.isPresent() ?
                    team.get().getMembers().contains(optionalMember.get())
                    : false;
            if (teamContainsMember) {
                return ResponseEntity.ok(optionalMember.get());
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(
            @PathVariable("teamId") UUID teamId,
            @PathVariable("memberId") UUID memberId
    ) {
        Optional<Team> teamOptional = teamRepo.findById(teamId);
        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            Set<Member> teamMembers = team.getMembers();
            Optional<Member> toDelete = memberRepo.findById(memberId);
            if (toDelete.isPresent()) {
                teamMembers.remove(toDelete.get());
                team.setMembers(teamMembers);
                teamRepo.save(team);
                return ResponseEntity.noContent().build();
            }
//            return ResponseEntity.status(HttpStatus.GONE).build();
        }
        return ResponseEntity.notFound().build();
    }

}

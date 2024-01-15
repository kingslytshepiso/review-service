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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import tech.ioco.review.repository.RoleRepository;
import tech.ioco.review.repository.TeamRepository;
import tech.ioco.review.repository.MemberRepository;
import tech.ioco.review.entity.Role;
import tech.ioco.review.entity.Team;
import tech.ioco.review.entity.Member;

@RestController
@RequestMapping("/teams/{teamId}/members")
public class MemberController {
    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private MemberRepository memberRepo;

    @Autowired
    private RoleRepository roleRepo;

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

    @PostMapping
    public ResponseEntity<Void> createMember(@PathVariable("teamId") UUID teamId,
                                             @RequestBody Member model,
                                             UriComponentsBuilder ucb) {
        Optional<Team> teamOptional = teamRepo.findById(teamId);
        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            /* An alternative is to find a member by email
               which can eliminate the possibility of
               duplicates
             */
            Optional<Member> member = model.getId() != null ?
                    memberRepo.findById(model.getId())
                    : Optional.empty();
            Member newMember = model;
            Set<Member> members = team.getMembers();
            if (member.isEmpty()) {//if the member instance does not exist in the database
                newMember = createMember(model);
            }
            members.add(newMember);
            team.setMembers(members);
            Team savedTeam = teamRepo.save(team);
            URI groupLocation = ucb.path("teams/{teamsId}/members/{memberId}")
                    .buildAndExpand(savedTeam.getId(), newMember.getId()).toUri();
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
            //  return ResponseEntity.status(HttpStatus.GONE).build();
        }
        return ResponseEntity.notFound().build();
    }

}

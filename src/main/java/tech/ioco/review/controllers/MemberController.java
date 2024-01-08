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
import tech.ioco.review.data.MemberRepository;
import tech.ioco.review.entity.Team;
import tech.ioco.review.entity.Member;

@RestController
@RequestMapping("/groups/{groupId}/members")
public class MemberController {
    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private MemberRepository memberRepo;

    @GetMapping
    public ResponseEntity<Set<Member>> geAllMembers(
            @PathVariable("groupdId") UUID groupId) {
        Optional<Team> group = teamRepo.findById(groupId);
        if (group.isPresent()) {
            return ResponseEntity.ok(group.get().getMembers());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> createMember(@RequestBody UUID userId,
            @PathVariable("groupId") UUID groupId,
            @RequestParam("memberId") UUID memberId,
            UriComponentsBuilder ucb) {
        Optional<Team> group = teamRepo.findById(memberId);
        Boolean memberExists = memberRepo.existsById(memberId);
        if (group.isPresent() && memberExists) {
            Optional<Member> member = memberRepo.findById(memberId);
            group.get().addMember(member.get());
            URI groupLocation = ucb.path("groups/{groupId}/members")
                    .buildAndExpand(group.get().getId()).toUri();
            return ResponseEntity.created(groupLocation).build();
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<Member> getMember(@PathVariable("memberId") UUID id) {
        Optional<Member> member = memberRepo.findById(id);
        if (member.isPresent()) {
            return ResponseEntity.ok(member.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // The following request endpoint assumes that this
    // process involves updating a member and there
    // is no connection to the group during the update
    @PutMapping("/{memberId}")
    public ResponseEntity<Void> updateMember(@PathVariable("memberId") UUID id,
            @RequestBody Member model) {
        Optional<Member> member = memberRepo.findById(id);
        if (member.isPresent()) {
            model.setId(id);
            memberRepo.save(model);
            // return ResponseEntity.noContent().build();
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable("memberId") UUID id) {
        memberRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

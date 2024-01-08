package tech.ioco.review.controllers;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tech.ioco.review.data.TeamRepository;
import tech.ioco.review.data.ReviewRepository;
import tech.ioco.review.entity.Team;
import tech.ioco.review.entity.Review;

@RestController
@RequestMapping("/groups/{groupId}/reviews")
public class ReviewController {
    @Autowired
    private TeamRepository groupRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @GetMapping
    public ResponseEntity<Set<Review>> getAllReviews(
            @PathVariable("groupdId") UUID groupId) {
        Optional<Team> group = groupRepo.findById(groupId);
        if (group.isPresent()) {
            return ResponseEntity.ok(group.get().getReviews());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}

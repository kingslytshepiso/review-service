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

import tech.ioco.review.repository.TeamRepository;
import tech.ioco.review.repository.ReviewRepository;
import tech.ioco.review.entity.Team;
import tech.ioco.review.entity.Review;

@RestController
@RequestMapping("/teams/{teamId}/reviews")
public class TeamReviewController {
    @Autowired
    private TeamRepository teamRepo;

    @Autowired
    private ReviewRepository reviewRepo;

    @GetMapping
    public ResponseEntity<Set<Review>> getAllReviews(
            @PathVariable("teamId") UUID teamId) {
        Optional<Team> team = teamRepo.findById(teamId);
        if (team.isPresent()) {
            return ResponseEntity.ok(team.get().getReviews());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //The Post Request is ignored

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReview(
            @PathVariable("teamId") UUID teamId,
            @PathVariable("reviewId") UUID reviewId) {
        Optional<Team> teamOptional = teamRepo.findById(teamId);
        if (teamOptional.isPresent()) {
            Optional<Review> reviewOptional = reviewRepo.findById(reviewId);
            boolean teamContainsReview = reviewOptional.isPresent() ?
                    teamOptional.get().getReviews().contains(reviewOptional.get())
                    : false;
            if (teamContainsReview) {
                return ResponseEntity.ok(reviewOptional.get());
            }
        }
        return ResponseEntity.notFound().build();
    }

}

package tech.ioco.review.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import tech.ioco.review.entity.Review;
import tech.ioco.review.entity.Team;
import tech.ioco.review.repository.ReviewRepository;
import tech.ioco.review.repository.TeamRepository;

import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/reviews")
public class ReviewController {
    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private TeamRepository teamRepo;

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewRepo.findAll());
    }

    /*
     * Method verifies the list of teams and returns non empty
     * optional of all the teams that have a database instance*/
    public Set<Team> resolveTeams(Review model) {
        List<Team> teams = new ArrayList<>();
        model.getTeams().forEach(t -> {
            if (t.getId() != null) {
                Optional<Team> team = teamRepo.findById(t.getId());
                if (team.isPresent())
                    teams.add(team.get());
            }
        });
        return new HashSet<>(teams);
    }

    @PostMapping
    public ResponseEntity<Void> createReviewRequest(
            @RequestBody Review model,
            UriComponentsBuilder ucb) {
        model.setTeams(resolveTeams(model));
        Review savedReview = reviewRepo.save(model);
        Set<Team> teams = model.getTeams();
        savedReview.setTeams(new HashSet<>());
        teams.forEach(t -> {
            Set<Review> reviews = t.getReviews();
            reviews.add(savedReview);
            t.setReviews(reviews);
            teamRepo.save(t);
        });
        URI reviewLocation = ucb.path("reviews/{reviewId}")
                .buildAndExpand(savedReview.getId()).toUri();
        return ResponseEntity.created(reviewLocation).build();
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<Review> getReview(@PathVariable UUID reviewId) {
        Optional<Review> reviewOptional = reviewRepo.findById(reviewId);
        if (reviewOptional.isPresent())
            return ResponseEntity.ok(reviewOptional.get());
        else
            return ResponseEntity.notFound().build();
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable("reviewId") UUID reviewId,
            @RequestBody Review model) {
        Optional<Review> reviewOptional = reviewRepo.findById(reviewId);
        if (reviewOptional.isPresent()) {
            model.setTeams(resolveTeams(model));
            Review savedReview = reviewRepo.save(model);
            Set<Team> teams = model.getTeams();
            savedReview.setTeams(new HashSet<>());
            teams.forEach(t -> {
                Set<Review> reviews = t.getReviews();
                reviews.add(savedReview);
                t.setReviews(reviews);
                teamRepo.save(t);
            });
            return ResponseEntity.noContent().build();
        } else
            return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable("reviewId") UUID reviewId) {
        Optional<Review> reviewOptional = reviewRepo.findById(reviewId);
        if (reviewOptional.isPresent()) {
            reviewRepo.delete(reviewOptional.get());
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();

    }


}

package tech.ioco.review.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Serves as the endpoint for the dashboard page
@RestController
@RequestMapping("/")
public class HomeController {
    @GetMapping
    public ResponseEntity<Void> getHome() {
        return ResponseEntity.ok().build();
    }
}

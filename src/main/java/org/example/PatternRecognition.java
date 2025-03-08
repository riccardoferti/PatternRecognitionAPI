package org.example;

import java.util.*;

import jakarta.validation.Valid;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SpringBootApplication
@RestController
public class PatternRecognition {
    public static void main(String[] args) {
        SpringApplication.run(PatternRecognition.class, args);
    }

    @PostMapping("/point")
    public ResponseEntity<String> createPoint(@Valid @RequestBody Point point) {
        if (Space.containsPoint(point)) {
            return ResponseEntity.badRequest().body("Point already exists!");
        }
        Space.addPoint(point);
        return ResponseEntity.ok("Point added successfully");
    }

    @GetMapping("/space")
    public ArrayList<Point> getSpace() {
        return Space.points;
    }

    @DeleteMapping("/space")
    public ResponseEntity<String> deleteSpace() {
        Space.delete();
        return ResponseEntity.ok("All points deleted");
    }

    @GetMapping("/lines/{n}")
    public ResponseEntity<?> getLines(@PathVariable("n") int n) {
        if (n < 2) {
            return ResponseEntity.badRequest().body("N must be at least 2");
        }
        if (n > Space.points.size()) {
            return ResponseEntity.badRequest().body("N cannot be greater than the number of points set");
        }
        return ResponseEntity.ok(Space.getLines(n));
    }
}



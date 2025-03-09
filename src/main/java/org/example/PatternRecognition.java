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
    public ResponseEntity<?> createPoint(@Valid @RequestBody Point point) {

        //Custom validation could be done in this case using a custom JSON deserialization
        //Now duplicated keys are working yet in this way

        //Checks if the point is already present
        if (Space.containsPoint(point)) {
            Map<String, String> error = new HashMap<>();
            //Every response will have a key with success or error and a related message
            error.put("error", "Point already exists!");
            return ResponseEntity.badRequest().body(error);
        }
        Space.addPoint(point);
        Map<String, String> success = new HashMap<>();
        success.put("message", "Point added successfully");
        return ResponseEntity.ok(success);
    }

    @GetMapping("/space")
    public ResponseEntity<List<Point>> getSpace() {
        return ResponseEntity.ok(Space.points);
    }

    @DeleteMapping("/space")
    public ResponseEntity<Map<String, String>> deleteSpace() {
        Space.delete();
        Map<String, String> success = new HashMap<>();
        success.put("message", "All points deleted");
        return ResponseEntity.ok(success);
    }

    @GetMapping("/lines/{n}")
    public ResponseEntity<?> getLines(@PathVariable("n") int n) {
        if (n < 2) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "N must be at least 2");
            return ResponseEntity.badRequest().body(error);
        }
        if (n > Space.points.size()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "N cannot be greater than the number of points set");
            return ResponseEntity.badRequest().body(error);
        }
        return ResponseEntity.ok(Space.getLines(n));
    }
}



package org.example;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootApplication
@RestController
@Validated
public class PatternRecognition {
    public static void main(String[] args) {
        SpringApplication.run(PatternRecognition.class, args);
    }

    @PostMapping("/point")
    public ResponseEntity<String> createPoint(@RequestBody Point point) {
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


class Point {
    private int x;
    private int y;

    public Point() {
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Point point = (Point) obj;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}

class Space {
    static ArrayList<Point> points = new ArrayList<>();

    static void addPoint(Point p) {
        points.add(p);
    }

    static boolean containsPoint(Point p) {
        return points.contains(p);
    }

    static void delete() {
        points.clear();
    }

    static List<Set<Point>> getLines(int n) {
        Map<String, Set<Point>> lineMap = new HashMap<>();

        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);

            for (int j = i + 1; j < points.size(); j++) {
                Point p2 = points.get(j);
                String key = getLineKey(p1, p2);

                lineMap.computeIfAbsent(key, k -> new HashSet<>()).add(p1);
                lineMap.get(key).add(p2);
            }
        }

        return lineMap.values().stream()
                .filter(pointSet -> pointSet.size() >= n)
                .collect(Collectors.toList());
    }

    static String getLineKey(Point p1, Point p2) {
        if (p1.getX() == p2.getX()) {
            return "v" + p1.getX();
        }
        if (p1.getY() == p2.getY()) {
            return "h" + p1.getY();
        }

        double m = (double) (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
        double b = p1.getY() - m * p1.getX();

        return String.format("%.10f:%.10f", m, b);
    }
}

class PatternRecognitionTest {
    @BeforeEach
    void setUp() {
        Space.delete();
    }

    @Test
    void testAddPoint() {
        Point p = new Point(1, 2);
        ResponseEntity<String> response = new PatternRecognition().createPoint(p);
        assertEquals("Point added successfully", response.getBody());
        assertTrue(Space.containsPoint(p));
    }

    @Test
    void testAddDuplicatePoint() {
        Point p = new Point(1, 2);
        Space.addPoint(p);
        ResponseEntity<String> response = new PatternRecognition().createPoint(p);
        assertEquals("Point already exists!", response.getBody());
    }

    @Test
    void testGetSpace() {
        Point p1 = new Point(1, 2);
        Point p2 = new Point(3, 4);
        Space.addPoint(p1);
        Space.addPoint(p2);
        List<Point> points = new PatternRecognition().getSpace();
        assertEquals(2, points.size());
        assertTrue(points.contains(p1));
        assertTrue(points.contains(p2));
    }

    @Test
    void testDeleteSpace() {
        Space.addPoint(new Point(1, 2));
        new PatternRecognition().deleteSpace();
        assertEquals(0, Space.points.size());
    }

    @Test
    void testGetLines_Valid() {
        Space.addPoint(new Point(0, 0));
        Space.addPoint(new Point(1, 1));
        Space.addPoint(new Point(2, 2));
        Space.addPoint(new Point(0, 0));
        Space.addPoint(new Point(0, 0));
        Space.addPoint(new Point(1, 2));
        Space.addPoint(new Point(2, 4));
        Space.addPoint(new Point(-2, 4));
        Space.addPoint(new Point(2, -4));
        Space.addPoint(new Point(12, 4));
        ResponseEntity<?> response = new PatternRecognition().getLines(2);
        List<Set<Point>> lines = (List<Set<Point>>) response.getBody();
        assertNotNull(lines);
        assertFalse(lines.isEmpty());
    }

    @Test
    void testGetLines_InvalidN() {
        ResponseEntity<?> response = new PatternRecognition().getLines(1);
        assertEquals("N must be at least 2", response.getBody());
    }

    @Test
    void testGetLines_TooHighN() {
        Space.addPoint(new Point(0, 0));
        ResponseEntity<?> response = new PatternRecognition().getLines(3);
        assertEquals("N cannot be greater than the number of points set", response.getBody());
    }
}

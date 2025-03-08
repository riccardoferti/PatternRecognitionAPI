package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class PatternRecognitionTest {
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

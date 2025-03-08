package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Space {
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

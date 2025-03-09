package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Space {
    //Not so efficient, but as requested to not using DB storage, this could be sufficient
    static ArrayList<Point> points = new ArrayList<>();

    static void addPoint(Point p) {
        points.add(p);
    }

    static boolean containsPoint(Point p) {
        return points.contains(p);
    }

    //Delete implemented recycling static reference of points collection
    static void delete() {
        points.clear();
    }

    //Returns a list of set points that build the segments
    static List<Set<Point>> getLines(int n) {
        Map<String, Set<Point>> lineMap = new HashMap<>();

        for (int i = 0; i < points.size(); i++) {
            Point p1 = points.get(i);

            for (int j = i + 1; j < points.size(); j++) {
                Point p2 = points.get(j);
                //Generates a unique string key for the line
                String key = getLineKey(p1, p2);

                lineMap.computeIfAbsent(key, k -> new HashSet<>()).add(p1);
                lineMap.get(key).add(p2);
            }
        }

        return lineMap.values().stream()
                .filter(pointSet -> pointSet.size() >= n)
                .collect(Collectors.toList());
    }

    //Returns the string used as key based on angular coefficient and intercept of lines
    static String getLineKey(Point p1, Point p2) {
        //Where lines are parallel to Y axes
        if (p1.getX() == p2.getX()) {
            return "v" + p1.getX();
        }
        //Where lines are parallel to X axes
        if (p1.getY() == p2.getY()) {
            return "h" + p1.getY();
        }
        //angular coefficient
        double m = (double) (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
        //intercept
        double b = p1.getY() - m * p1.getX();

        return String.format("%.10f:%.10f", m, b);
    }
}

package dev.mrcai.clustering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DBSCAN {

    private static class Point {
        public double x;
        public double y;
        public boolean isVisited;
        public int clusterId;

        public Point(double x, double y) {
            this.x = x;
            this.y = y;
            isVisited = false;
            clusterId = -1;
        }
    }

    private List<Point> points;
    private double epsilon;
    private int minNeighborNum;

    public static void main(String args[]) {
        DBSCAN dbscan = new DBSCAN("data/clustering/points.txt", 1.5, 2);
        dbscan.run();
    }

    public DBSCAN(String filePath, double epsilon, int minNeighborNum) {
        points = loadPoints(filePath);
        this.epsilon = epsilon;
        this.minNeighborNum = minNeighborNum;
    }

    public void run() {
        int currentClusterId = 0;
        for (Point point : points) {
            // Pick an unvisited point.
            if (point.isVisited) {
                continue;
            }
            point.isVisited = true;

            // Get the neighbors of the point.
            List<Point> neighbors = getNeighbors(point);

            // If there is not enough neighbors, the point is not a center.
            if (neighbors.size() < minNeighborNum) {
                continue;
            }

            // Otherwise, start with this center and form a new cluster.
            currentClusterId++;
            point.clusterId = currentClusterId;

            // Use BFS to explore all reachable points in the cluster.
            Queue<Point> reachableQueue = new LinkedList<Point>(neighbors);
            while (!reachableQueue.isEmpty()) {
                // Fetch a point from the queue.
                Point currentPoint = reachableQueue.poll();

                // If this point is already visited, skip it.
                if (currentPoint.isVisited) {
                    continue;
                }
                currentPoint.isVisited = true;

                // Add this point to the cluster.
                currentPoint.clusterId = currentClusterId;

                // If this point is not a center, stop further exploration from this point.
                List<Point> currentNeighbors = getNeighbors(currentPoint);
                if (currentNeighbors.size() < minNeighborNum) {
                    continue;
                }

                // Otherwise, mark all of its neighbors as reachable.
                for (Point currentNeighbor : currentNeighbors) {
                    // If this neighbor is already visited or is already in the queue, skip it.
                    // This is not necessary, but it can save some time.
                    if (currentNeighbor.isVisited || reachableQueue.contains(currentNeighbor)) {
                        continue;
                    }
                    reachableQueue.add(currentNeighbor);
                }
            }
        }

        printPoints(points);
    }

    private List<Point> getNeighbors(Point center) {
        List<Point> neighbors = new ArrayList<Point>();
        for (Point point : points) {
            if (point == center) {
                continue;
            }
            if (getDistance(center, point) > epsilon) {
                continue;
            }
            neighbors.add(point);
        }
        return neighbors;
    }

    private static List<Point> loadPoints(String filePath) {
        List<Point> points = new ArrayList<Point>();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] stringPoint = line.trim().split("\t");
                Point point = new Point(Double.parseDouble(stringPoint[0]), Double.parseDouble(stringPoint[1]));
                points.add(point);
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return points;
    }

    private static double getDistance(Point point1, Point point2) {
        return Math.sqrt(Math.pow(point1.x - point2.x, 2) + Math.pow(point1.y - point2.y, 2));
    }

    private static void printPoints(List<Point> points) {
        for (Point point : points) {
            System.out.println("(" + point.x + ", " + point.y + ")\t => " + point.clusterId);
        }
    }
}

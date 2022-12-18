package dev.mrcai.datamining.clustering;

import java.util.ArrayList;
import java.util.List;

public class DBSCAN {
    private static class Point extends dev.mrcai.datamining.clustering.Point {
        private boolean isVisited = false;

        public Point(double x, double y) {
            super(x, y);
        }

        public boolean getIsVisited() {
            return isVisited;
        }

        public void setIsVisited(boolean isVisited) {
            this.isVisited = isVisited;
        }
    }

    private List<Point> points = new ArrayList<Point>();

    /**
     * The maximum distance between two points to be considered as neighbors.
     */
    private double epsilon = 0.0;

    /**
     * The minimum number of neighbors a point should have
     * to be considered as a center.
     */
    private int minNeighborNum = 0;

    /**
     * The index of the cluster currently being explored.
     */
    private int currentClusterIndex = 0;

    public static void main(String args[]) {
        DBSCAN dbscan = new DBSCAN("data/points.txt", 1.5, 2);
        dbscan.plot("outputs/dbscan.png");
    }

    public DBSCAN(String filePath, double epsilon, int minNeighborNum) {
        points = IOUtils.loadPoints(filePath, Point.class);
        this.epsilon = epsilon;
        this.minNeighborNum = minNeighborNum;
        cluster();
    }

    public void plot(String filePath) {
        IOUtils.plotPoints(points, filePath);
    }

    private void cluster() {
        for (Point point : points) {
            // If this point is already visited, skip it.
            if (point.getIsVisited()) {
                continue;
            }

            // If this point is not a center, don't visit it.
            // It is either a noise point or an edge point of a certain cluster.
            List<Point> neighbors = getNeighbors(point);
            if (neighbors.size() < minNeighborNum) {
                continue;
            }

            // Otherwise, start a new cluster.
            currentClusterIndex++;

            // Add this point to the cluster, and explore all of its neighbors.
            point.setIsVisited(true);
            point.setClusterIndex(currentClusterIndex);
            for (Point neighbor : neighbors) {
                exploreCluster(neighbor);
            }
        }
    }

    private void exploreCluster(Point point) {
        // If this point is already visited, skip it.
        if (point.getIsVisited()) {
            return;
        }

        // Add this point to the cluster.
        point.setIsVisited(true);
        point.setClusterIndex(currentClusterIndex);

        // If this point is not a center, stop further exploration from this point.
        List<Point> neighbors = getNeighbors(point);
        if (neighbors.size() < minNeighborNum) {
            return;
        }

        // Otherwise, continue to explore all of its neighbors.
        for (Point neighbor : neighbors) {
            exploreCluster(neighbor);
        }
    }

    private List<Point> getNeighbors(Point center) {
        List<Point> neighbors = new ArrayList<Point>();
        for (Point point : points) {
            if (point == center || point.getDistance(center) > epsilon) {
                continue;
            }
            neighbors.add(point);
        }
        return neighbors;
    }
}

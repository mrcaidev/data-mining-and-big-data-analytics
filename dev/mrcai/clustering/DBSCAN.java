package dev.mrcai.clustering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * DBSCAN clustering algorithm.
 */
public class DBSCAN {
    /**
     * Point in DBSCAN clustering.
     */
    private static class Point extends AbstractPoint {
        /**
         * Whether this point has been visited.
         * Every point is by default unvisited, and will be visited at most once.
         */
        private boolean isVisited = false;

        /**
         * Construct a point with the given x and y coordinates.
         *
         * @param x The x coordinate of the point.
         * @param y The y coordinate of the point.
         */
        public Point(double x, double y) {
            super(x, y);
        }

        /**
         * Get whether this point has been visited.
         *
         * @return Whether this point has been visited.
         */
        public boolean getIsVisited() {
            return isVisited;
        }

        /**
         * Set whether this point has been visited.
         *
         * @param isVisited Whether this point has been visited.
         */
        public void setIsVisited(boolean isVisited) {
            this.isVisited = isVisited;
        }
    }

    /**
     * Points to be clustered.
     */
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
     * Cluster the points in the given file, and visualize the result.
     */
    public static void main(String args[]) {
        DBSCAN dbscan = new DBSCAN("data/clustering/points.txt", 1.5, 2);
        dbscan.run();
    }

    /**
     * Initialize the DBSCAN algorithm with the given parameters.
     *
     * @param filePath       The path of the file that contains the points to be
     *                       clustered.
     * @param epsilon        The maximum distance between two points to be
     *                       considered as neighbors.
     * @param minNeighborNum The minimum number of neighbors a point should have
     *                       to be considered as a center.
     */
    public DBSCAN(String filePath, double epsilon, int minNeighborNum) {
        loadPoints(filePath);
        this.epsilon = epsilon;
        this.minNeighborNum = minNeighborNum;
    }

    /**
     * Load the points from the given file.
     *
     * @param filePath The path of the file that contains the points to be
     *                 clustered.
     */
    private void loadPoints(String filePath) {
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
    }

    /**
     * Run the DBSCAN algorithm.
     */
    public void run() {
        // Record the index of the current cluster.
        int currentClusterIndex = 0;

        // Inspect every unvisited point.
        for (Point point : points) {
            if (point.getIsVisited()) {
                continue;
            }
            point.setIsVisited(true);

            // Get the neighbors of the point.
            List<Point> neighbors = getNeighbors(point);

            // If there is not enough neighbors, the point is not a center.
            if (neighbors.size() < minNeighborNum) {
                continue;
            }

            // Otherwise, start with this center and form a new cluster.
            currentClusterIndex++;
            point.setClusterIndex(currentClusterIndex);

            // Use BFS to explore all reachable points in the cluster.
            Queue<Point> reachableQueue = new LinkedList<Point>(neighbors);
            while (!reachableQueue.isEmpty()) {
                // Fetch a point from the queue.
                Point currentPoint = reachableQueue.poll();

                // If this point is already visited, skip it.
                if (currentPoint.getIsVisited()) {
                    continue;
                }
                currentPoint.setIsVisited(true);

                // Add this point to the cluster.
                currentPoint.setClusterIndex(currentClusterIndex);

                // If this point is not a center, stop further exploration from this point.
                List<Point> currentNeighbors = getNeighbors(currentPoint);
                if (currentNeighbors.size() < minNeighborNum) {
                    continue;
                }

                // Otherwise, mark all of its neighbors as reachable.
                for (Point currentNeighbor : currentNeighbors) {
                    // If this neighbor is already visited or is already in the queue, skip it.
                    // This is not necessary, but it can save some time.
                    if (currentNeighbor.getIsVisited() || reachableQueue.contains(currentNeighbor)) {
                        continue;
                    }
                    reachableQueue.add(currentNeighbor);
                }
            }
        }

        // Visualize the result.
        Plot<Point> plot = new Plot<Point>(points);
        plot.visualize("DBSCAN clustering");
    }

    /**
     * Get the neighbors of the given point.
     *
     * @param center The point.
     * @return The neighbors of the given point.
     */
    private List<Point> getNeighbors(Point center) {
        List<Point> neighbors = new ArrayList<Point>();
        for (Point point : points) {
            if (point == center) {
                continue;
            }
            if (point.getDistance(center) > epsilon) {
                continue;
            }
            neighbors.add(point);
        }
        return neighbors;
    }
}

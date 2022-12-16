package dev.mrcai.datamining.clustering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * K Means clustering algorithm.
 */
public class KMeans {
    /**
     * The points to be clustered.
     */
    private List<Point> points = new ArrayList<Point>();

    /**
     * The centers of the clusters.
     */
    private List<Point> centers = new ArrayList<Point>();

    /**
     * Cluster the points in the given file, and visualize the result.
     */
    public static void main(String args[]) {
        KMeans kMeans = new KMeans("data/clustering/points.txt", 3);
        kMeans.run();
    }

    /**
     * Create a K means clustering algorithm with the given points and cluster
     * number.
     *
     * @param filePath   The path of the file that stores the points.
     * @param clusterNum The number of clusters.
     */
    KMeans(String filePath, int clusterNum) {
        loadPoints(filePath);
        initializeCenters(clusterNum);
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
                String[] pointString = line.trim().split("\t");
                double x = Double.parseDouble(pointString[0]);
                double y = Double.parseDouble(pointString[1]);
                Point point = new Point(x, y);
                points.add(point);
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Initialize the centers of the clusters at random points.
     *
     * @param clusterNum The number of clusters.
     */
    private void initializeCenters(int clusterNum) {
        for (int clusterIndex = 0; clusterIndex < clusterNum; clusterIndex++) {
            centers.add(createRandomPoint());
        }
    }

    /**
     * Run the K means clustering algorithm.
     */
    private void run() {
        while (true) {
            List<Point> nextCenters = getNextCenters();
            if (isSameCenters(nextCenters)) {
                break;
            }
            centers = nextCenters;
        }

        Plot<Point> plot = new Plot<Point>("K means clustering", points);
        plot.save("outputs/clustering/kmeans.png");
    }

    /**
     * Get the centers of the clusters in the next iteration.
     *
     * @return The next centers of the clusters.
     */
    private List<Point> getNextCenters() {
        // Store the next centers' coordinates.
        List<Point> nextCenters = new ArrayList<Point>();
        for (int centerIndex = 0; centerIndex < centers.size(); centerIndex++) {
            nextCenters.add(new Point(0, 0));
        }

        // Record the number of points in each next cluster.
        int clusterNum = centers.size();
        int[] pointNums = new int[clusterNum];

        // Every point goes to its closest center.
        for (Point point : points) {
            int closestCenterIndex = getClosestCenterIndex(point);

            // Assign the point to the cluster.
            point.setClusterIndex(closestCenterIndex);
            pointNums[closestCenterIndex]++;

            // Update the next center's coordinates.
            // The current coordinates are the sum of all points' coordinates
            // in the cluster, used to calculate the mean value later.
            Point nextCenter = nextCenters.get(closestCenterIndex);
            nextCenter.setX(nextCenter.getX() + point.getX());
            nextCenter.setY(nextCenter.getY() + point.getY());
        }

        // Generate new centers.
        for (int clusterIndex = 0; clusterIndex < clusterNum; clusterIndex++) {
            // If there is no point in the cluster, spawn this center randomly again.
            if (pointNums[clusterIndex] == 0) {
                nextCenters.set(clusterIndex, createRandomPoint());
                continue;
            }

            // Otherwise, calculate the mean of the cluster, and treat it as the new center.
            Point nextCenter = nextCenters.get(clusterIndex);
            nextCenter.setX(nextCenter.getX() / pointNums[clusterIndex]);
            nextCenter.setY(nextCenter.getY() / pointNums[clusterIndex]);
        }

        return nextCenters;
    }

    /**
     * Get the index of the closest center to the given point.
     *
     * @param point The point to be clustered.
     * @return The index of the closest center.
     */
    private int getClosestCenterIndex(Point point) {
        double minDistance = Double.MAX_VALUE;
        int closestCenterIndex = -1;
        for (int centerIndex = 0; centerIndex < centers.size(); centerIndex++) {
            double distance = point.getDistance(centers.get(centerIndex));
            if (distance >= minDistance) {
                continue;
            }
            minDistance = distance;
            closestCenterIndex = centerIndex;
        }
        return closestCenterIndex;
    }

    /**
     * Check if the next centers are the same as the current centers.
     *
     * @param nextCenters The centers to be checked.
     * @return True if the centers are the same, false otherwise.
     */
    private boolean isSameCenters(List<Point> nextCenters) {
        for (int centerIndex = 0; centerIndex < centers.size(); centerIndex++) {
            Point center = centers.get(centerIndex);
            Point nextCenter = nextCenters.get(centerIndex);
            if (center.getX() != nextCenter.getX() || center.getY() != nextCenter.getY()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Create a random point.
     *
     * @return The random point.
     */
    private static Point createRandomPoint() {
        return new Point(Math.random() * 100, Math.random() * 100);
    }
}

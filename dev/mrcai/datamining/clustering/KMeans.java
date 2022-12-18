package dev.mrcai.datamining.clustering;

import java.util.ArrayList;
import java.util.List;

public class KMeans {
    private List<Point> points = new ArrayList<Point>();
    private List<Point> centers = new ArrayList<Point>();

    public static void main(String args[]) {
        KMeans kMeans = new KMeans("data/points.txt", 3);
        kMeans.plot("outputs/kmeans.png");
    }

    public KMeans(String filePath, int clusterNum) {
        points = IOUtils.loadPoints(filePath, Point.class);
        initializeCenters(clusterNum);
        cluster();
    }

    public void plot(String filePath) {
        IOUtils.plotPoints(points, filePath);
    }

    private void initializeCenters(int clusterNum) {
        for (int clusterIndex = 0; clusterIndex < clusterNum; clusterIndex++) {
            centers.add(createRandomPoint());
        }
    }

    private void cluster() {
        while (true) {
            List<Point> nextCenters = getNextCenters();
            if (isSamePoints(centers, nextCenters)) {
                break;
            }
            centers = nextCenters;
        }
    }

    private List<Point> getNextCenters() {
        // Store the next centers' coordinates.
        List<Point> nextCenters = new ArrayList<Point>();
        for (int centerIndex = 0; centerIndex < centers.size(); centerIndex++) {
            nextCenters.add(new Point(0, 0));
        }

        // Record the number of points in each next cluster.
        int clusterNum = centers.size();
        int[] pointNums = new int[clusterNum];

        for (Point point : points) {
            // Find the closest center.
            int closestCenterIndex = getClosestCenterIndex(point);

            // Assign the point to the cluster.
            point.setClusterIndex(closestCenterIndex);
            pointNums[closestCenterIndex]++;

            // Update the center's coordinates.
            // The current coordinates are the sum of all points' coordinates
            // in the cluster, used to calculate the mean value later.
            Point nextCenter = nextCenters.get(closestCenterIndex);
            nextCenter.setX(nextCenter.getX() + point.getX());
            nextCenter.setY(nextCenter.getY() + point.getY());
        }

        // Calculate the coordinates of the next centers.
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

    private boolean isSamePoints(List<Point> pointsA, List<Point> pointsB) {
        if (pointsA.size() != pointsB.size()) {
            return false;
        }

        for (int pointIndex = 0; pointIndex < pointsA.size(); pointIndex++) {
            Point pointA = pointsA.get(pointIndex);
            Point pointB = pointsB.get(pointIndex);
            if (pointA.getX() != pointB.getX() || pointA.getY() != pointB.getY()) {
                return false;
            }
        }
        return true;
    }

    private static Point createRandomPoint() {
        return new Point(Math.random() * 100, Math.random() * 100);
    }
}

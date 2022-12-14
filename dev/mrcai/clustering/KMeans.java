package dev.mrcai.clustering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

public class KMeans {
    List<double[]> points;
    double[][] centers;
    int clusterNum;

    public static void main(String args[]) {
        KMeans kMeans = new KMeans("data/clustering/points.txt", 3);
        kMeans.cluster();
    }

    KMeans(String filePath, int clusterNum) {
        points = loadDataSet(filePath);
        this.clusterNum = clusterNum;
        centers = createRandomCenters();
    }

    private void cluster() {
        while (true) {
            double[][] nextCenters = getNextCenters();
            if (isSameCenters(nextCenters)) {
                break;
            }
            centers = nextCenters;
        }

        printCenters();
        visualize();
    }

    private double[][] createRandomCenters() {
        int dimensionNum = points.get(0).length;
        double[][] centers = new double[clusterNum][dimensionNum];

        for (int clusterIndex = 0; clusterIndex < clusterNum; clusterIndex++) {
            double[] center = new double[dimensionNum];
            for (int dimensionIndex = 0; dimensionIndex < dimensionNum; dimensionIndex++) {
                center[dimensionIndex] = getRandomNumber();
            }
            centers[clusterIndex] = center;
        }

        return centers;
    }

    private int getClosestCenterIndex(double[] point) {
        double minDistance = Double.MAX_VALUE;
        int closestCenterIndex = -1;
        for (int centerIndex = 0; centerIndex < centers.length; centerIndex++) {
            double distance = getDistance(centers[centerIndex], point);
            if (distance >= minDistance) {
                continue;
            }
            minDistance = distance;
            closestCenterIndex = centerIndex;
        }
        return closestCenterIndex;
    }

    private double[][] getNextCenters() {
        int dimensionNum = points.get(0).length;
        double[][] nextCenters = new double[clusterNum][dimensionNum];
        int[] clusterCounts = new int[clusterNum];

        // Every center collects its closest points.
        for (double[] point : points) {
            int closestClusterIndex = getClosestCenterIndex(point);
            clusterCounts[closestClusterIndex]++;
            for (int dim = 0; dim < dimensionNum; dim++) {
                nextCenters[closestClusterIndex][dim] += point[dim];
            }
        }

        // Generate new centers.
        for (int clusterIndex = 0; clusterIndex < clusterNum; clusterIndex++) {
            for (int dimensionIndex = 0; dimensionIndex < dimensionNum; dimensionIndex++) {
                // If there is no point in the cluster, spawn this center randomly again.
                if (clusterCounts[clusterIndex] == 0) {
                    nextCenters[clusterIndex][dimensionIndex] = getRandomNumber();
                    continue;
                }

                // Otherwise, calculate the mean of the cluster, and treat it as the new center.
                nextCenters[clusterIndex][dimensionIndex] /= clusterCounts[clusterIndex];
            }
        }

        return nextCenters;
    }

    private boolean isSameCenters(double[][] nextCenters) {
        for (int centerIndex = 0; centerIndex < centers.length; centerIndex++) {
            if (getDistance(centers[centerIndex], nextCenters[centerIndex]) != 0) {
                return false;
            }
        }
        return true;
    }

    private void printCenters() {
        for (int centerIndex = 0; centerIndex < centers.length; centerIndex++) {
            System.out.print("Center " + centerIndex + ": ");
            for (int dimensionIndex = 0; dimensionIndex < centers[centerIndex].length; dimensionIndex++) {
                double coordinate = Math.round(centers[centerIndex][dimensionIndex] * 100) / 100.0;
                System.out.print(coordinate + " ");
            }
            System.out.println();
        }
    }

    private void visualize() {
        // Group the points by their closest center.
        List<List<double[]>> clusters = new ArrayList<List<double[]>>();
        for (int clusterIndex = 0; clusterIndex < clusterNum; clusterIndex++) {
            clusters.add(new ArrayList<double[]>());
        }
        for (double[] point : points) {
            int closestClusterIndex = getClosestCenterIndex(point);
            clusters.get(closestClusterIndex).add(point);
        }

        // Transform the data to the format that JFreeChart can understand.
        int dimensionNum = points.get(0).length;
        DefaultXYDataset plotData = new DefaultXYDataset();
        for (int clusterIndex = 0; clusterIndex < clusterNum; clusterIndex++) {
            List<double[]> clusterPoints = clusters.get(clusterIndex);
            double[][] clusterPlotData = new double[dimensionNum][clusterPoints.size()];
            for (int pointIndex = 0; pointIndex < clusterPoints.size(); pointIndex++) {
                double[] point = clusterPoints.get(pointIndex);
                for (int dimensionIndex = 0; dimensionIndex < dimensionNum; dimensionIndex++) {
                    clusterPlotData[dimensionIndex][pointIndex] = point[dimensionIndex];
                }
            }
            plotData.addSeries(clusterIndex, clusterPlotData);
        }

        // Visualize the data.
        JFreeChart chart = ChartFactory.createScatterPlot("Clusters", "x", "y", plotData);
        ChartFrame frame = new ChartFrame("K Means Clustering", chart, true);
        frame.pack();
        frame.setVisible(true);
    }

    private static double getRandomNumber() {
        return Math.random() * 100;
    }

    private static double getDistance(double[] point1, double[] point2) {
        double sum = 0;
        for (int dimensionIndex = 0; dimensionIndex < point1.length; dimensionIndex++) {
            sum += Math.pow(point1[dimensionIndex] - point2[dimensionIndex], 2);
        }
        return Math.sqrt(sum);
    }

    private static List<double[]> loadDataSet(String filePath) {
        List<double[]> points = new ArrayList<double[]>();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] pointString = line.trim().split("\t");
                double[] point = new double[pointString.length];
                for (int i = 0; i < pointString.length; i++) {
                    point[i] = Double.parseDouble(pointString[i]);
                }
                points.add(point);
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return points;
    }
}

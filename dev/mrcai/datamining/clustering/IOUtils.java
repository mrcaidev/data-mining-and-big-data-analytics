package dev.mrcai.datamining.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

public class IOUtils {
    private IOUtils() {
    }

    public static <T extends Point> List<T> loadPoints(String filePath, Class<T> pointClass) {
        List<T> points = new ArrayList<T>();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] stringPoint = line.trim().split("\t");
                double x = Double.parseDouble(stringPoint[0]);
                double y = Double.parseDouble(stringPoint[1]);
                T point = pointClass.getDeclaredConstructor(double.class, double.class).newInstance(x, y);
                points.add(point);
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return points;
    }

    public static <T extends Point> void plotPoints(List<T> points, String filePath) {
        DefaultXYDataset plotData = transformPoints(points);
        JFreeChart chart = createChart(plotData);
        saveChart(chart, filePath);
    }

    private static <T extends Point> DefaultXYDataset transformPoints(List<T> points) {
        // Get the number of points in each cluster.
        Map<Integer, Integer> pointNums = getPointNums(points);
        int clusterNum = pointNums.size();

        // Initialize the array that stores the transformed points.
        double[][][] clusters = new double[clusterNum][2][];
        for (int clusterIndex = 0; clusterIndex < clusterNum; clusterIndex++) {
            int pointNum = pointNums.get(clusterIndex);
            clusters[clusterIndex][0] = new double[pointNum];
            clusters[clusterIndex][1] = new double[pointNum];
        }

        // Record the number of transformed points of each cluster.
        int[] transformedPointIndexes = new int[clusterNum];

        // Transform the points.
        for (Point point : points) {
            int clusterIndex = point.getClusterIndex();
            int pointIndex = transformedPointIndexes[clusterIndex];
            clusters[clusterIndex][0][pointIndex] = point.getX();
            clusters[clusterIndex][1][pointIndex] = point.getY();
            transformedPointIndexes[clusterIndex]++;
        }

        // Transform the data to the format that JFreeChart can understand.
        DefaultXYDataset plotData = new DefaultXYDataset();
        for (int clusterIndex = 0; clusterIndex < clusterNum; clusterIndex++) {
            plotData.addSeries(clusterIndex, clusters[clusterIndex]);
        }

        return plotData;
    }

    private static <T extends Point> Map<Integer, Integer> getPointNums(List<T> points) {
        Map<Integer, Integer> pointNums = new HashMap<Integer, Integer>();
        for (Point point : points) {
            int clusterIndex = point.getClusterIndex();
            if (pointNums.containsKey(clusterIndex)) {
                pointNums.put(clusterIndex, pointNums.get(clusterIndex) + 1);
            } else {
                pointNums.put(clusterIndex, 1);
            }
        }
        return pointNums;
    }

    private static JFreeChart createChart(DefaultXYDataset plotData) {
        JFreeChart chart = ChartFactory.createScatterPlot("Clusters", "x", "y", plotData);
        ChartFrame frame = new ChartFrame("Clusters", chart, true);
        frame.pack();
        frame.dispose();
        return chart;
    }

    public static void saveChart(JFreeChart chart, String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            ChartUtilities.saveChartAsPNG(file, chart, 500, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package dev.mrcai.datamining.clustering;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

/**
 * The plotter of points in clusters.
 */
public class Plot<T extends Point> {
    /**
     * The chart to be plotted.
     */
    private JFreeChart chart = null;

    /**
     * Create a chart with the given title and points.
     *
     * @param title  The title of the chart.
     * @param points The points to be plotted.
     */
    public Plot(String title, List<T> points) {
        DefaultXYDataset plotData = transformPoints(points);
        createChart(title, plotData);
    }

    /**
     * Transform the given points to the format that JFreeChart can understand.
     *
     * @param points The points to be transformed.
     */
    private DefaultXYDataset transformPoints(List<T> points) {
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

    /**
     * Get the number of points in each cluster.
     *
     * @param points The points to be counted.
     * @return The number of points in each cluster.
     */
    private Map<Integer, Integer> getPointNums(List<T> points) {
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

    /**
     * Create a chart with the given title and data.
     *
     * @param title    The title of the chart.
     * @param plotData The data to be plotted.
     */
    private void createChart(String title, DefaultXYDataset plotData) {
        chart = ChartFactory.createScatterPlot(title, "x", "y", plotData);
        ChartFrame frame = new ChartFrame(title, chart, true);
        frame.pack();
        frame.dispose();
    }

    /**
     * Save the plot to the given file path.
     *
     * @param filePath The file path to save the plot.
     */
    public void save(String filePath) {
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

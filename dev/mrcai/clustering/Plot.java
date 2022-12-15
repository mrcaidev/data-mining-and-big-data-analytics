package dev.mrcai.clustering;

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
 * The plotter of the points in clusters.
 */
public class Plot<T extends Point> {
    /**
     * The title of the plot.
     */
    String title = "Cluster";

    /**
     * The data that will be plotted, in the format that JFreeChart can understand.
     */
    private DefaultXYDataset plotData = new DefaultXYDataset();

    /**
     * Construct a plot with the given points.
     *
     * @param title  The title of the plot.
     * @param points The points to be plotted.
     */
    public Plot(String title, List<T> points) {
        this.title = title;
        getPlotData(points);
    }

    /**
     * Transform the given points to the format that JFreeChart can understand.
     *
     * The points after transformation looks like:
     * [
     * [[x1, x2, x3, ...], [y1, y2, y3, ...]], // Cluster 0
     * [[x1, x2, x3, ...], [y1, y2, y3, ...]], // Cluster 1
     * [[x1, x2, x3, ...], [y1, y2, y3, ...]], // Cluster 2
     * ...
     * ]
     *
     * @param points The points to be transformed.
     */
    private void getPlotData(List<T> points) {
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
        for (int clusterIndex = 0; clusterIndex < clusterNum; clusterIndex++) {
            plotData.addSeries(clusterIndex, clusters[clusterIndex]);
        }
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
     * Save the plot to the given file path.
     *
     * @param filePath The file path to save the plot.
     */
    public void save(String filePath) {
        JFreeChart chart = ChartFactory.createScatterPlot(title, "x", "y", plotData);
        ChartFrame frame = new ChartFrame(title, chart, true);
        frame.pack();

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            ChartUtilities.saveChartAsPNG(file, chart, 500, 500);
        } catch (Exception e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }
}

package dev.mrcai.clustering;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.DefaultXYDataset;

public class Utils {
    public static void show(double[][][] T, int numC) {
        double[][][] datas = new double[numC][][];
        for (int n = 0; n < numC; n++) {
            double[][] l = T[n];
            datas[n] = new double[2][l.length];
            for (int m = 0; m < l.length; m++) {
                datas[n][0][m] = l[m][0];
                datas[n][1][m] = l[m][1];
            }
        }

        DefaultXYDataset dataSet = new DefaultXYDataset();
        for (int n = 0; n < datas.length; n++) {
            dataSet.addSeries(" " + n, datas[n]);
        }
        JFreeChart chart = ChartFactory.createScatterPlot("Visualization", "x", "y", dataSet);
        ChartFrame frame = new ChartFrame("kmean ", chart, true);
        frame.pack();
        frame.setVisible(true);
    }
}

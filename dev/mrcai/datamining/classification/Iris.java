package dev.mrcai.datamining.classification;

import java.util.Arrays;

public class Iris {
    private double[] data = new double[4];
    private String label = "";

    public Iris(double[] data, String label) {
        this.data = data;
        this.label = label;
    }

    public double[] getData() {
        return data;
    }

    public String getLabel() {
        return label;
    }

    public double getDistance(Iris iris) {
        double sum = 0.0;
        for (int index = 0; index < data.length; index++) {
            sum += Math.pow(data[index] - iris.data[index], 2);
        }
        return Math.sqrt(sum);
    }

    public String toString() {
        return Arrays.toString(data) + " => " + label;
    }
}

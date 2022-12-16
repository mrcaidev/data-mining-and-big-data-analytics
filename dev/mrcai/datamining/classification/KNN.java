package dev.mrcai.datamining.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNN {
    private List<Data> trainSet;
    private int k;

    public static void main(String[] args) {
        KNN knn = new KNN("data/classification/train.csv", 3);
        knn.predict("data/classification/test.csv");
    }

    public KNN(String filePath, int k) {
        trainSet = loadDataSet(filePath);
        this.k = k;
    }

    public void predict(String filePath) {
        // Read test data set.
        List<Data> testSet = loadDataSet(filePath);

        // Predict each data in test data set.
        int correctCount = 0;
        for (Data testData : testSet) {
            String prediction = classify(testData);
            if (prediction.equals(testData.label)) {
                correctCount++;
                continue;
            }
            System.out.println("Wrong prediction: " + prediction + ", Actual: " + testData.label);
        }

        // Print accuracy.
        double accuracy = correctCount * 100.0 / testSet.size();
        System.out.println("Accuracy: " + accuracy + "%");
    }

    public String classify(Data testData) {
        // Calculate the distance between the test data and each training data.
        double[] distances = getDistances(testData);

        // Record the labels of the k nearest neighbors,
        // and the times they appear.
        Map<String, Integer> neighbors = new HashMap<String, Integer>();

        for (int num = 0; num < k; num++) {
            // Find the nearest neighbor.
            double minDistance = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < distances.length; i++) {
                if (distances[i] >= minDistance) {
                    continue;
                }
                minDistance = distances[i];
                minIndex = i;
            }

            // If the nearest neighbor is not in the map, break the loop.
            if (minIndex == -1) {
                break;
            }

            // Exclude this neighbor from the next search.
            distances[minIndex] = Integer.MAX_VALUE;

            // Record the label of the nearest neighbor.
            String label = trainSet.get(minIndex).label;
            if (neighbors.containsKey(label)) {
                neighbors.put(label, neighbors.get(label) + 1);
            } else {
                neighbors.put(label, 1);
            }
        }

        return getMostFrequentLabel(neighbors);
    }

    private double[] getDistances(Data testData) {
        double[] distances = new double[trainSet.size()];
        for (int i = 0; i < trainSet.size(); i++) {
            distances[i] = getDistance(testData, trainSet.get(i));
        }
        return distances;
    }

    private String getMostFrequentLabel(Map<String, Integer> neighbors) {
        String mostFrequentLabel = "";
        int maxCount = 0;
        for (String label : neighbors.keySet()) {
            int count = neighbors.get(label);
            if (count <= maxCount) {
                continue;
            }
            maxCount = count;
            mostFrequentLabel = label;
        }
        return mostFrequentLabel;
    }

    private static double getDistance(Data testData, Data trainData) {
        // Calculate the Euclidean distance.
        double sum = 0;
        for (int i = 0; i < testData.data.length; i++) {
            sum += Math.pow(testData.data[i] - trainData.data[i], 2);
        }
        return Math.sqrt(sum);
    }

    private static List<Data> loadDataSet(String filePath) {
        List<Data> dataSet = new ArrayList<Data>();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] rawData = line.trim().split(",");
                double[] data = new double[rawData.length - 1];
                for (int i = 0; i < rawData.length - 1; i++) {
                    data[i] = Double.parseDouble(rawData[i]);
                }
                dataSet.add(new Data(data, rawData[rawData.length - 1]));
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataSet;
    }
}

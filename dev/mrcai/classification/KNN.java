package dev.mrcai.classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class KNN {
    private class Data {
        double[] data;
        String label;

        Data(double[] data, String label) {
            this.data = data;
            this.label = label;
        }
    }

    ArrayList<Data> trainSet;
    int k;

    public static void main(String[] args) {
        KNN knn = new KNN("data/classification/train.csv", 3);
        knn.predict("data/classification/test.csv");
    }

    public KNN(String filePath, int k) {
        this.trainSet = this.loadDataSet(filePath);
        this.k = k;
    }

    private ArrayList<Data> loadDataSet(String filePath) {
        ArrayList<Data> dataSet = new ArrayList<Data>();

        try {
            File file = new File(filePath);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] dataString = line.trim().split(",");
                double[] data = new double[dataString.length - 1];
                for (int i = 0; i < dataString.length - 1; i++) {
                    data[i] = Double.parseDouble(dataString[i]);
                }
                dataSet.add(new Data(data, dataString[dataString.length - 1]));
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataSet;
    }

    public void predict(String filePath) {
        // Read test data set.
        ArrayList<Data> testSet = this.loadDataSet(filePath);

        // Predict each data in test data set.
        int correct = 0;
        for (Data data : testSet) {
            String prediction = this.classify(data);
            if (prediction.equals(data.label)) {
                correct++;
                continue;
            }
            System.out.println("Wrong prediction: " + prediction + ", Actual: " + data.label);
        }

        // Print accuracy.
        double accuracy = (double) correct / testSet.size() * 100;
        System.out.println("Accuracy: " + accuracy + "%");
    }

    public String classify(Data testData) {
        // Calculate the distance between the test data and each training data.
        double[] distances = new double[this.trainSet.size()];
        for (int i = 0; i < this.trainSet.size(); i++) {
            distances[i] = this.getDistance(testData, this.trainSet.get(i));
        }

        // Sort the distances and get the k nearest neighbors.
        HashMap<String, Integer> voteMap = new HashMap<String, Integer>();
        for (int num = 0; num < this.k; num++) {
            // Find the nearest neighbor.
            double minDistance = Integer.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < this.trainSet.size(); i++) {
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

            // Add the label of the nearest neighbor to the vote map.
            String label = this.trainSet.get(minIndex).label;
            if (voteMap.containsKey(label)) {
                voteMap.put(label, voteMap.get(label) + 1);
            } else {
                voteMap.put(label, 1);
            }
        }

        // Find the label with the most votes.
        Iterator<String> key = voteMap.keySet().iterator();
        String prediction = "";
        int maxVote = 0;
        while (key.hasNext()) {
            String label = key.next();
            int vote = voteMap.get(label);
            if (vote > maxVote) {
                maxVote = vote;
                prediction = label;
            }
        }

        return prediction;
    }

    private double getDistance(Data testData, Data trainData) {
        // Calculate the Euclidean distance.
        double sum = 0;
        for (int i = 0; i < testData.data.length; i++) {
            sum += Math.pow(testData.data[i] - trainData.data[i], 2);
        }
        return Math.sqrt(sum);
    }
}

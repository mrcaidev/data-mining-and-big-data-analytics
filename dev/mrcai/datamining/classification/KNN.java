package dev.mrcai.datamining.classification;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class KNN {

    /**
     * When finding the k closest irises,
     * each train iris is regarded as a neighbor.
     */
    private static class Neighbor {
        public Iris iris;
        public double distance;

        public Neighbor(Iris iris, double distance) {
            this.iris = iris;
            this.distance = distance;
        }
    }

    private List<Iris> trainIrises;
    private int k;

    public static void main(String[] args) {
        KNN knn = new KNN("data/classification/train.csv", 3);
        knn.test("data/classification/test.csv");
    }

    public KNN(String filePath, int k) {
        trainIrises = IOUtils.loadIrises(filePath);
        this.k = k;
    }

    public void test(String filePath) {
        // Load test irises.
        List<Iris> testIrises = IOUtils.loadIrises(filePath);

        // Classify each iris.
        int correctCount = 0;
        for (Iris testIris : testIrises) {
            String prediction = classify(testIris);
            if (testIris.getLabel().equals(prediction)) {
                correctCount++;
                continue;
            }
            IOUtils.printPrediction(testIris, prediction);
        }

        // Print the accuracy.
        double accuracy = correctCount * 1.0 / testIrises.size();
        IOUtils.printAccuracy(accuracy);
    }

    private String classify(Iris iris) {
        List<Iris> kClosestIrises = getKClosestIrises(iris);
        return getMostFrequentLabel(kClosestIrises);
    }

    private List<Iris> getKClosestIrises(Iris iris) {
        // Store the k closest irises.
        // The closest iris is at the tail of the queue.
        PriorityQueue<Neighbor> neighbors = new PriorityQueue<Neighbor>(k, new Comparator<Neighbor>() {
            @Override
            public int compare(Neighbor o1, Neighbor o2) {
                return -Double.compare(o1.distance, o2.distance);
            }
        });

        // Find the k closest irises.
        for (Iris trainIris : trainIrises) {
            double distance = trainIris.getDistance(iris);

            // If the queue is not full, add the neighbor.
            if (neighbors.size() < k) {
                neighbors.add(new Neighbor(trainIris, distance));
                continue;
            }

            // If the neighbor is farther, skip it.
            if (distance >= neighbors.peek().distance) {
                continue;
            }

            // Otherwise, remove the farthest neighbor and add this one.
            neighbors.poll();
            neighbors.add(new Neighbor(trainIris, distance));
        }

        // Transform queue to list.
        List<Iris> kClosestIrises = new ArrayList<Iris>();
        while (!neighbors.isEmpty()) {
            kClosestIrises.add(neighbors.poll().iris);
        }

        return kClosestIrises;
    }

    private static String getMostFrequentLabel(List<Iris> irises) {
        // Count the number of each label.
        Map<String, Integer> labelCount = new HashMap<String, Integer>();
        for (Iris iris : irises) {
            String label = iris.getLabel();
            if (labelCount.containsKey(label)) {
                labelCount.put(label, labelCount.get(label) + 1);
            } else {
                labelCount.put(label, 1);
            }
        }

        // Find the most frequent label.
        int maxCount = 0;
        String mostFrequentLabel = null;
        for (String label : labelCount.keySet()) {
            int count = labelCount.get(label);
            if (count <= maxCount) {
                continue;
            }
            maxCount = count;
            mostFrequentLabel = label;
        }

        return mostFrequentLabel;
    }
}

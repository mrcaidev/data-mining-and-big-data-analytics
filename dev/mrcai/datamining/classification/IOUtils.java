package dev.mrcai.datamining.classification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class IOUtils {
    private IOUtils() {
    }

    public static List<Iris> loadIrises(String filePath) {
        List<Iris> irises = new ArrayList<Iris>();

        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] stringIris = line.trim().split(",");
                double[] data = new double[stringIris.length - 1];
                for (int index = 0; index < stringIris.length - 1; index++) {
                    data[index] = Double.parseDouble(stringIris[index]);
                }
                String label = stringIris[stringIris.length - 1];
                Iris iris = new Iris(data, label);
                irises.add(iris);
            }

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return irises;
    }

    public static void printPrediction(Iris iris, String prediction) {
        System.out.println(iris.toString() + "\tprediction=" + prediction);
    }

    public static void printAccuracy(double accuracy) {
        double accuracyRounded = Math.floor(accuracy * 10000.0) / 100.0;
        System.out.println("Accuracy: " + accuracyRounded + "%");
    }
}

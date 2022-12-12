package dev.mrcai.clustering;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class KMeans {
    ArrayList<double[]> dataSet;
    int clusterNum;
    int dimension;

    KMeans(int clusterNum) {
        this.dataSet = new ArrayList<double[]>();
        this.loadDataSet("data/clustering/testSet.txt");
        this.clusterNum = clusterNum;
    }

    public static void main(String args[]) {
        KMeans kMeans = new KMeans(3);
        kMeans.cluster();
    }

    private void cluster() {
        Random rand = new Random();
        double[][] clusterMeans = new double[clusterNum][dimension];

        // random initialize mean of cluster
        for (int i = 0; i < clusterNum; i++) {
            double[] data = new double[dimension];
            for (int dim = 0; dim < dimension; dim++) {
                data[dim] = rand.nextDouble() * 100;
            }
            clusterMeans[i] = data;
        }

        boolean shouldContinue = true;
        while (shouldContinue) {
            shouldContinue = false;
            double[][] nextClusterMeans = new double[clusterNum][dimension];
            int[] clusterDataNum = new int[clusterNum];

            // Assign cluster mean to every data.
            for (int n = 0; n < this.dataSet.size(); n++) {
                double mindistance = Double.MAX_VALUE;
                int cluster = -1;
                for (int m = 0; m < clusterNum; m++) {
                    double distance = this.getDistance(clusterMeans[m], this.dataSet.get(n));
                    if (distance < mindistance) {
                        cluster = m;
                        mindistance = distance;
                    }
                }
                clusterDataNum[cluster]++;
                for (int i = 0; i < dimension; i++) {
                    nextClusterMeans[cluster][i] += this.dataSet.get(n)[i];
                }
            }

            // Update cluster means.
            for (int i = 0; i < clusterNum; i++) {
                for (int j = 0; j < dimension; j++) {
                    if (clusterDataNum[i] != 0) {
                        nextClusterMeans[i][j] /= clusterDataNum[i];
                    } else {
                        nextClusterMeans[i][j] = Math.random() * 100;
                    }
                }
            }

            // If there is no big difference between nextClusterMeans and clusterMeans,
            // break the loop.
            for (int i = 0; i < clusterNum; i++) {
                if (this.getDistance(nextClusterMeans[i], clusterMeans[i]) != 0) {
                    shouldContinue = true;
                }
            }
            clusterMeans = nextClusterMeans;
        }

        // Visualization.
        ArrayList<ArrayList<double[]>> clusters = new ArrayList<ArrayList<double[]>>();
        for (int n = 0; n < clusterNum; n++) {
            clusters.add(new ArrayList<double[]>());
        }
        for (int n = 0; n < this.dataSet.size(); n++) {
            double minDistance = Double.MAX_VALUE;
            int cluster = -1;
            for (int m = 0; m < clusterNum; m++) {
                double distance = this.getDistance(clusterMeans[m], this.dataSet.get(n));
                if (distance < minDistance) {
                    cluster = m;
                    minDistance = distance;
                }
            }
            clusters.get(cluster).add(this.dataSet.get(n));
        }
        double[][][] datas = new double[clusterNum][][];
        for (int n = 0; n < clusterNum; n++) {
            double[][] cluster = new double[clusters.get(n).size()][];
            for (int m = 0; m < cluster.length; m++) {
                cluster[m] = clusters.get(n).get(m);
            }
            datas[n] = cluster;
        }
        System.out.println("cluster mean:");
        for (int n = 0; n < clusterMeans.length; n++) {
            for (double x : clusterMeans[n]) {
                System.out.print(x + " ");
            }
            System.out.println();
        }
        Utils.show(datas, clusterNum);
    }

    private double getDistance(double[] testData, double[] trainData) {
        double sum = 0;
        for (int i = 0; i < testData.length; i++) {
            sum += Math.pow(testData[i] - trainData[i], 2);
        }
        return Math.sqrt(sum);
    }

    private void loadDataSet(String filePath) {
        try {
            File file = new File(filePath);
            FileReader fr = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fr);

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] dataString = line.trim().split("\t");
                double[] data = new double[dataString.length];
                for (int i = 0; i < data.length; i++) {
                    data[i] = Double.parseDouble(dataString[i]);
                }
                this.dataSet.add(data);
            }
            this.dimension = this.dataSet.get(0).length;

            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

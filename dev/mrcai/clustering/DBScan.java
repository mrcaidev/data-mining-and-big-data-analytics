package dev.mrcai.clustering;

import java.util.ArrayList;

public class DBScan {

    private ArrayList<Point> Dataset;
    private int eps;
    private int minpts;

    public DBScan(ArrayList<Point> D, int e, int m) {
        Dataset = (ArrayList<Point>) D.clone();
        eps = e;
        minpts = m;
    }

    public ArrayList<Point> run() {
        int c = 0;
        for (int i = 0; i < Dataset.size(); i++) {
            Dataset.get(i).visit();
            ArrayList<Integer> Neighbors = regionQuery(Dataset.get(i));
            if (Neighbors.size() >= minpts) {
                c = c + 1;
                expandCluster(Dataset.get(i), Neighbors, c);
            } else {
                Dataset.get(i).setClusterId(-1);
            }
        }
        return Dataset;
    }

    private static ArrayList createSampleData() {
        ArrayList<Point> data = new ArrayList<Point>();
        data.add(new Point(9, 9));
        data.add(new Point(8, 9));
        data.add(new Point(9, 8));
        data.add(new Point(8, 8));
        data.add(new Point(1, 1));
        data.add(new Point(7, 7));
        return data;
    }

    private void expandCluster(Point p, ArrayList<Integer> Neighbors, int c) {
        p.setClusterId(c);
        for (int i = 0; i < Neighbors.size(); i++) {
            if (!Dataset.get(Neighbors.get(i)).getIsVisited()) {
                Dataset.get(Neighbors.get(i)).visit();
                ArrayList<Integer> TmpNeighbors = regionQuery(Dataset.get(Neighbors.get(i)));
                if (TmpNeighbors.size() >= minpts) {
                    for (Integer j : TmpNeighbors) {
                        Neighbors.add(j);
                    }
                }
            }
            if (Dataset.get(Neighbors.get(i)).getClusterId() != -1) {
                Dataset.get(Neighbors.get(i)).setClusterId(c);
            }
        }
    }

    private ArrayList regionQuery(Point p) {
        ArrayList<Integer> region = new ArrayList<Integer>();
        for (int i = 0; i < Dataset.size(); i++) {
            if (getDistance(p, Dataset.get(i)) <= eps) {
                region.add(i);
            }
        }
        return region;
    }

    private int getDistance(Point a, Point b) {
        int dx;
        int dy;
        if (a.getX() > b.getX()) {
            dx = a.getX() - b.getX();
        } else {
            dx = b.getX() - a.getX();
        }
        if (a.getY() > b.getY()) {
            dy = a.getY() - b.getY();
        } else {
            dy = b.getY() - a.getY();
        }
        return dx + dy;
    }

    public static void main(String args[]) {
        DBScan d = new DBScan(createSampleData(), 2, 2);
        ArrayList<Point> output = d.run();
        for (Point p : output) {
            System.out.println("X: " + p.getX() + "\tY: " + p.getY() + "\t ClusterID: " + p.getClusterId());
        }
    }

}

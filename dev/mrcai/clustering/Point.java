package dev.mrcai.clustering;

public class Point {
    private int x;
    private int y;
    private boolean isVisited;
    private int clusterId;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.isVisited = false;
        this.clusterId = -1;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public boolean getIsVisited() {
        return this.isVisited;
    }

    public void visit() {
        this.isVisited = true;
    }

    public int getClusterId() {
        return this.clusterId;
    }

    public void setClusterId(int id) {
        this.clusterId = id;
    }
}

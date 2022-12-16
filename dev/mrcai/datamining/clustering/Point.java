package dev.mrcai.datamining.clustering;

/**
 * A 2D point in a cluster.
 */
public class Point {
    /**
     * The x coordinate of the point.
     */
    private double x = 0;

    /**
     * The y coordinate of the point.
     */
    private double y = 0;

    /**
     * The cluster index of the point.
     * Every point is by default in cluster 0.
     */
    private int clusterIndex = 0;

    /**
     * Construct a point with the given x and y coordinates.
     *
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x coordinate of the point.
     *
     * @return The x coordinate of the point.
     */
    public double getX() {
        return x;
    }

    /**
     * Set the x coordinate of the point.
     *
     * @param x The x coordinate of the point.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Get the y coordinate of the point.
     *
     * @return The y coordinate of the point.
     */
    public double getY() {
        return y;
    }

    /**
     * Set the y coordinate of the point.
     *
     * @param y The y coordinate of the point.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the cluster index of the point.
     *
     * @return The cluster index of the point.
     */
    public int getClusterIndex() {
        return clusterIndex;
    }

    /**
     * Set the cluster index of the point.
     *
     * @param clusterIndex The cluster index of the point.
     */
    public void setClusterIndex(int clusterIndex) {
        this.clusterIndex = clusterIndex;
    }

    /**
     * Get the distance between this point and the given point.
     *
     * @param point The given point.
     * @return The distance between this point and the given point.
     */
    public double getDistance(Point point) {
        return Math.sqrt(Math.pow(x - point.getX(), 2) + Math.pow(y - point.getY(), 2));
    }
}

package dev.mrcai.clustering;

/**
 * The abstract class of a common 2D point.
 */
public abstract class AbstractPoint implements Point {
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
    public AbstractPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getClusterIndex() {
        return clusterIndex;
    }

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

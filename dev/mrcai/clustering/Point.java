package dev.mrcai.clustering;

/**
 * The interface of a 2D point.
 */
public interface Point {
    /**
     * Get the x coordinate of the point.
     *
     * @return The x coordinate of the point.
     */
    double getX();

    /**
     * Set the x coordinate of the point.
     *
     * @param x The x coordinate of the point.
     */
    void setX(double x);

    /**
     * Get the y coordinate of the point.
     *
     * @return The y coordinate of the point.
     */
    double getY();

    /**
     * Set the y coordinate of the point.
     *
     * @param y The y coordinate of the point.
     */
    void setY(double y);

    /**
     * Get the cluster index of the point.
     *
     * @return The cluster index of the point.
     */
    int getClusterIndex();

    /**
     * Set the cluster index of the point.
     *
     * @param clusterIndex The cluster index of the point.
     */
    void setClusterIndex(int clusterIndex);
}

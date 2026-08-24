package com.aethervault.storage.lattice;

/**
 * Defines the minimum and maximum coordinates that bound a Voxel Lattice.
 */
public class BoundingBox {
    private final Vector3d minCorner; // Minimum corner (e.g., bottom-left-back)
    private final Vector3d maxCorner; // Maximum corner (e.g., top-right-front)

    public BoundingBox(Vector3d minCorner, Vector3d maxCorner) {
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
    }

    // Getters...
    public Vector3d getMinCorner() { return minCorner; }
    public Vector3d getMaxCorner() { return maxCorner; }

    /**
     * Checks if a given coordinate falls within the defined bounds (inclusive).
     */
    public boolean contains(Vector3d point) {
        return point.getX() >= minCorner.getX() && point.getX() <= maxCorner.getX() &&
               point.getY() >= minCorner.getY() && point.getY() <= maxCorner.getY() &&
               point.getZ() >= minCorner.getZ() && point.getZ() <= maxCorner.getZ();
    }

    // Helper method to calculate volume or dimensions could be added here later.
}
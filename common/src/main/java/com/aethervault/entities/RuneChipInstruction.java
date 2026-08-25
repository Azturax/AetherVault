package com.aethervault.entities;

/**
 * Defines a specific behavior or "rune-chip" instruction assigned to a Familiar Agent.
 */
public class RuneChipInstruction {
    public enum Role {
        GATHER, // Search for and collect items matching criteria
        COURIER, // Move between two points (source/destination)
        DEFENDER, // Patrol an area or guard a specific block entity
        SORT // Sort incoming items based on internal rules
    }

    private final Role role;
    private final String targetTag; // e.g., "ore", "wood" for GATHER roles
    private final Vector3d destination; // Target location for COURIER/DEFENDER roles
    private final int maxCapacity; // Capacity limit if the familiar is a carrier

    public RuneChipInstruction(Role role, String targetTag, Vector3d destination, int maxCapacity) {
        this.role = role;
        this.targetTag = targetTag;
        this.destination = destination;
        this.maxCapacity = maxCapacity;
    }

    // Getters...
    public Role getRole() { return role; }
    public String getTargetTag() { return targetTag; }
    public Vector3d getDestination() { return destination; }
    public int getMaxCapacity() { return maxCapacity; }
}
package com.aethervault.entities;

import com.aethervault.core.Vector3d;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

/**
 * Defines a specific behavior or "rune-chip" instruction assigned to a Familiar Agent.
 */
public class RuneChipInstruction {

    public enum Role {
        GATHER,   // Search for and collect items matching criteria
        COURIER,  // Move between two points (source/destination)
        DEFENDER, // Patrol an area or guard a specific block entity
        SORT      // Sort incoming items based on internal rules
    }

    private final Role role;
    private final String targetTag;     // e.g., "minecraft:logs", "ore" for GATHER roles
    private final Vector3d destination; // Target location for COURIER/DEFENDER roles
    private final int maxCapacity;      // Capacity limit if the familiar is a carrier

    public RuneChipInstruction(Role role, String targetTag, Vector3d destination, int maxCapacity) {
        this.role = role;
        this.targetTag = targetTag;
        this.destination = destination;
        this.maxCapacity = maxCapacity;
    }

    public Role getRole() {
        return role;
    }

    public String getTargetTag() {
        return targetTag;
    }

    public Vector3d getDestination() {
        return destination;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    /**
     * Returns the next role in the cycling order used by block/item interactions.
     */
    public static Role nextRole(Role current) {
        Role[] roles = Role.values();
        if (current == null) {
            return roles[0];
        }
        return roles[(current.ordinal() + 1) % roles.length];
    }

    // ------------------------------------------------------------- NBT ---

    private static final String TAG_ROLE = "Role";
    private static final String TAG_TARGET_TAG = "TargetTag";
    private static final String TAG_DEST = "Destination";
    private static final String TAG_CAPACITY = "MaxCapacity";

    /** Serializes this instruction under the given root tag. */
    public void save(CompoundTag root) {
        CompoundTag tag = new CompoundTag();
        tag.putString(TAG_ROLE, role.name());
        if (targetTag != null) {
            tag.putString(TAG_TARGET_TAG, targetTag);
        }
        if (destination != null) {
            CompoundTag dest = new CompoundTag();
            dest.putDouble("X", destination.getX());
            dest.putDouble("Y", destination.getY());
            dest.putDouble("Z", destination.getZ());
            tag.put(TAG_DEST, dest);
        }
        tag.putInt(TAG_CAPACITY, maxCapacity);
        root.put("Task", tag);
    }

    /** Deserializes an instruction from a root tag, or null when absent. */
    public static RuneChipInstruction load(CompoundTag root) {
        if (!root.contains("Task", Tag.TAG_COMPOUND)) {
            return null;
        }
        CompoundTag tag = root.getCompound("Task");
        Role role;
        try {
            role = Role.valueOf(tag.getString(TAG_ROLE));
        } catch (IllegalArgumentException ex) {
            return null;
        }
        String targetTag = tag.contains(TAG_TARGET_TAG) ? tag.getString(TAG_TARGET_TAG) : null;
        Vector3d destination = null;
        if (tag.contains(TAG_DEST, Tag.TAG_COMPOUND)) {
            CompoundTag dest = tag.getCompound(TAG_DEST);
            destination = new Vector3d(dest.getDouble("X"), dest.getDouble("Y"), dest.getDouble("Z"));
        }
        int capacity = tag.getInt(TAG_CAPACITY);
        return new RuneChipInstruction(role, targetTag, destination, capacity);
    }
}
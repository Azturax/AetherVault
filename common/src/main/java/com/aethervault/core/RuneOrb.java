package com.aethervault.core;

import net.minecraft.item.ItemStack;
import java.util.UUID;

/**
 * Represents a stored item within the Voxel Lattice.
 */
public class RuneOrb {
    private final ItemStack stack;
    private final UUID uniqueId; // Unique ID for tracking this specific orb instance
    private long timestamp; // Time of storage/last modification

    public RuneOrb(ItemStack stack) {
        this.stack = stack;
        this.uniqueId = UUID.randomUUID();
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and setters...
    public ItemStack getStack() { return stack; }
    public UUID getUniqueId() { return uniqueId; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
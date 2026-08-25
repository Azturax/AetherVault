package com.aethervault.logic;

import net.minecraft.world.item.ItemStack;

/**
 * A concrete implementation of RuneCondition that checks if an item's durability falls below a specified threshold.
 */
public class DurabilityThresholdCondition implements RuneCondition {
    private final int requiredDurabilityPercentage; // e.g., 50 for 50% remaining

    public DurabilityThresholdCondition(int requiredDurabilityPercentage) {
        this.requiredDurabilityPercentage = requiredDurabilityPercentage;
    }

    @Override
    public boolean matches(ItemStack item) {
        // Placeholder logic: Check if the current durability is less than the threshold percentage of max durability.
        return checkDurabilityThreshold(item, requiredDurabilityPercentage); 
    }

    private boolean checkDurabilityThreshold(ItemStack item, int threshold) {
        int maxDamage = item.getMaxDamage();
        int currentDamage = item.getDamage();
        
        if (maxDamage <= 0) return false; // Cannot check durability on non-durable items

        // Calculate percentage remaining: (Max - Current Damage) / Max * 100
        double durabilityRemainingPercent = ((double)(maxDamage - currentDamage) / maxDamage) * 100.0;
        
        return durabilityRemainingPercent < threshold;
    }

    @Override
    public String getDescription() {
        return "Durability below " + requiredDurabilityPercentage + "%";
    }
}
package com.aethervault.logic;

import net.minecraft.world.item.ItemStack;

/**
 * Defines a condition that an item must meet to pass through a specific node in the RuneGraph.
 */
public interface RuneCondition {
    /**
     * Evaluates whether the given ItemStack satisfies this condition.
     * @param item The item being evaluated.
     * @return true if the item meets the criteria, false otherwise.
     */
    boolean matches(ItemStack item);

    /**
     * Returns a human-readable description of what this condition checks for (for UI display).
     */
    String getDescription();
}
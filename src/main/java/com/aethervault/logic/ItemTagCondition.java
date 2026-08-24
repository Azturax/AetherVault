package com.aethervault.logic;

import net.minecraft.world.item.ItemStack;

/**
 * A concrete implementation of RuneCondition that checks if an item possesses a specific tag or metadata.
 */
public class ItemTagCondition implements RuneCondition {
    private final String requiredTag;

    public ItemTagCondition(String requiredTag) {
        this.requiredTag = requiredTag;
    }

    @Override
    public boolean matches(ItemStack item) {
        // Placeholder for actual tag/metadata checking logic (e.g., using NBT or custom tags).
        return item.hasCustomTag(requiredTag); 
    }

    @Override
    public String getDescription() {
        return "Has Tag: '" + requiredTag + "'";
    }
}
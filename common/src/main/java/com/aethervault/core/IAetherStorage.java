package com.aethervault.core;

import net.minecraft.world.item.ItemStack;
import java.util.UUID;

/**
 * Defines the common interface for all AetherVault storage mechanisms, 
 * ensuring modularity and allowing different storage types to be treated uniformly.
 */
public interface IAetherStorage {
    
    /**
     * Attempts to store an item in this storage mechanism.
     * @param item The ItemStack to store.
     * @return true if the item was successfully stored, false otherwise (e.g., full).
     */
    boolean store(ItemStack item);

    /**
     * Retrieves a specific item from the storage.
     * @param uniqueId The ID of the item/slot being retrieved.
     * @return An Optional containing the ItemStack if found, or empty otherwise.
     */
    java.util.Optional<ItemStack> retrieve(UUID uniqueId);

    /**
     * Checks if there is space available for a new item.
     * @param item The item to check capacity against.
     * @return true if storage can accommodate the item, false otherwise.
     */
    boolean hasSpace(ItemStack item);

    /**
     * Clears all items from this storage mechanism.
     */
    void clear();
}